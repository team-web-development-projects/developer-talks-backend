package com.dtalks.dtalks.board.comment.service;

import com.dtalks.dtalks.board.comment.dto.CommentInfoDto;
import com.dtalks.dtalks.board.comment.dto.UserCommentDto;
import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.board.comment.dto.CommentRequestDto;
import com.dtalks.dtalks.board.comment.repository.CommentRepository;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.entity.Notification;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.repository.NotificationRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActivityType;
import com.dtalks.dtalks.user.repository.ActivityRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ActivityRepository activityRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public CommentInfoDto searchById(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND_ERROR, "존재하지 않는 댓글입니다.");
        }
        Comment comment = optionalComment.get();
        return CommentInfoDto.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentInfoDto> searchListByPostId(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        List<Comment> commentList = commentRepository.findByPostId(postId);
        List<CommentInfoDto> commentInfoDtoList = new ArrayList<>();
        Map<Long, CommentInfoDto> map = new HashMap<>();

        commentList.stream().forEach(c -> {
            CommentInfoDto dto = CommentInfoDto.toDto(c);
            if (dto.getParentId() != null) {
                Long startComment = getParentComment(c).getId();
                map.get(startComment).getChildrenList().add(dto);
            } else {
                map.put(dto.getId(), dto);
                commentInfoDtoList.add(dto);
            }
        });

        return commentInfoDtoList;
    }

    private Comment getParentComment(Comment comment) {
        Comment parent = comment.getParent();
        if (parent != null) {
            return getParentComment(parent);
        }
        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCommentDto> searchListByUserId(String userId) {
        User user = findUser(userId);
        List<Comment> commentList = commentRepository.findByUserIdAndRemovedFalse(user.getId());
        return commentList.stream().map(c -> {
            Post post = c.getPost();
            return UserCommentDto.toDto(c, post.getId(), post.getTitle());
        }).toList();
    }

    @Override
    @Transactional
    public void saveComment(Long postId, CommentRequestDto dto) {
        User user = SecurityUtil.getUser();
        Post post = findPost(postId);
        post.plusCommentCount();

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .secret(dto.isSecret())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(comment);
        activityRepository.save(Activity.createBoard(user, post, comment, ActivityType.COMMENT));
        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(comment.getId(), post.getId(), post.getUser(),
                NotificationType.COMMENT, messageSource.getMessage("notification.post.comment", new Object[]{post.getTitle()}, null)));
    }

    @Override
    @Transactional
    public void saveReComment(Long postId, Long parentId, CommentRequestDto dto) {
        User user = SecurityUtil.getUser();
        Comment parentComment = findComment(parentId);
        if (postId != parentComment.getPost().getId()) {
            throw new ValidationException("부모 댓글과 자식 댓글의 게시글 번호가 일치하지 않습니다.");
        }

        Post post = findPost(postId);
        post.plusCommentCount();

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .secret(dto.isSecret())
                .user(user)
                .post(post)
                .parent(parentComment)
                .build();

        commentRepository.save(comment);
        activityRepository.save(Activity.createBoard(user, post, comment, ActivityType.COMMENT));

        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(comment.getId(), post.getId(), post.getUser(),
                NotificationType.COMMENT, messageSource.getMessage("notification.post.comment", new Object[]{post.getTitle()}, null)));
        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(comment.getId(), post.getId(), parentComment.getUser(),
                NotificationType.RECOMMENT, messageSource.getMessage("notification.post.recomment", new Object[]{post.getTitle()}, null)));
    }


    @Override
    @Transactional
    public void updateComment(Long id, CommentRequestDto dto) {
        User user = SecurityUtil.getUser();

        Comment comment = findComment(id);
        findPost(comment.getPost().getId());

        String currentUserId = user.getUserid();
        if (!comment.getUser().getUserid().equals(currentUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 댓글을 수정할 권한이 없습니다.");
        }
        comment.updateComment(dto.getContent(), dto.isSecret());
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        User user = SecurityUtil.getUser();
        Comment comment = findComment(id);
        String currentUserId = user.getUserid();
        if (!comment.getUser().getUserid().equals(currentUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 댓글을 수정할 권한이 없습니다.");
        }

        Optional<Activity> optionalActivity = activityRepository.findByCommentId(comment.getId());
        if (optionalActivity.isEmpty()) {
            throw new CustomException(ErrorCode.ACTIVITY_NOT_FOUND_ERROR, "해당 댓글 활동을 찾을 수 없습니다.");
        }
        Activity activity = optionalActivity.get();
        activity.setComment(null);

        Post post = comment.getPost();
        post.minusCommentCount();

        Notification notification = notificationRepository.findByRefIdAndType(comment.getId(), NotificationType.COMMENT)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND_ERROR, "해당하는 알림이 존재하지 않습니다."));
        if (notification.getReadStatus().equals(ReadStatus.READ)) {
            notification.readDataDeleteSetting();
        } else {
            notificationRepository.deleteById(notification.getId());
        }

        Optional<Notification> recommentNoti = notificationRepository.findByRefIdAndType(comment.getId(), NotificationType.RECOMMENT);
        if (recommentNoti.isPresent()) {
            if (notification.getReadStatus().equals(ReadStatus.READ)) {
                notification.readDataDeleteSetting();
            } else {
                notificationRepository.deleteById(notification.getId());
            }
        }

        /**
         * 삭제하려는 댓글의 자식 댓글이 있는 경우
         * db에서 삭제가 아닌 '삭제된 댓글입니다.'로 표시하기 위해 removed = true로 변경
         */
        if (comment.getChildList().size() != 0) {
            comment.setRemoved(true);
        }
        else {
            commentRepository.delete(getDeletableParentComment(comment));
        }
    }

    private Comment getDeletableParentComment(Comment comment) {
        Comment parent = comment.getParent();
        if (parent != null && parent.getChildList().size() == 1 && parent.isRemoved()) {
            return getDeletableParentComment(parent);
        }
        return comment;
    }

    @Transactional(readOnly = true)
    private User findUser(String userid) {
        Optional<User> user = userRepository.findByUserid(userid);
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }
        return user.get();
    }

    @Transactional(readOnly = true)
    private Post findPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }
        return post.get();
    }

    @Transactional(readOnly = true)
    private Comment findComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND_ERROR, "존재하지 않는 댓글입니다.");
        }
        return comment.get();
    }

}
