package com.dtalks.dtalks.board.comment.service;

import com.dtalks.dtalks.board.comment.dto.CommentInfoDto;
import com.dtalks.dtalks.board.comment.dto.UserCommentDto;
import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.board.comment.dto.CommentRequestDto;
import com.dtalks.dtalks.board.comment.repository.CommentRepository;
import com.dtalks.dtalks.board.comment.repository.CustomCommentRepository;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActivityType;
import com.dtalks.dtalks.user.repository.ActivityRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final CustomCommentRepository customCommentRepository;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ActivityRepository activityRepository;

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

        List<Comment> commentList = customCommentRepository.findAllByPost(optionalPost.get());
        List<CommentInfoDto> commentInfoDtoList = new ArrayList<>();
        Map<Long, CommentInfoDto> map = new HashMap<>();

        commentList.stream().forEach(c -> {
            CommentInfoDto dto = CommentInfoDto.toDto(c);
            Comment parent = c.getParent();
            if (parent != null) {
                dto.setParentId(parent.getId());
            }
            map.put(dto.getId(), dto);
            if (parent != null) {
                map.get(parent.getId()).getChildrenList().add(dto);
            } else {
                commentInfoDtoList.add(dto);
            }
        });

        return commentInfoDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCommentDto> searchListByUserId(String userId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(userId));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();
        List<Comment> commentList = commentRepository.findByUserIdAndIsRemovedFalse(user.getId());
        return commentList.stream().map(c -> {
            Post post = c.getPost();
            return UserCommentDto.toDto(c, post.getId(), post.getTitle());
        }).toList();
    }

    @Override
    @Transactional
    public void saveComment(Long postId, CommentRequestDto dto) {
        User user = SecurityUtil.getUser();
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .isSecret(dto.isSecret())
                .user(user)
                .post(post.get())
                .build();

        commentRepository.save(comment);

        // 댓글 활동을 사용자 기록에 추가
        Activity activity = Activity.builder()
                .post(post.get())
                .comment(comment)
                .type(ActivityType.COMMENT)
                .user(user)
                .build();

        activityRepository.save(activity);
    }

    @Override
    @Transactional
    public void saveReComment(Long postId, Long parentId, CommentRequestDto dto) {
        User user = SecurityUtil.getUser();
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        Optional<Comment> parentComment = commentRepository.findById(parentId);
        if (parentComment.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND_ERROR, "존재하지 않는 댓글입니다.");
        }

        if (postId != parentComment.get().getPost().getId()) {
            throw new ValidationException("부모 댓글과 자식 댓글의 게시글 번호가 일치하지 않습니다.");
        }

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .isSecret(dto.isSecret())
                .user(user)
                .post(post.get())
                .parent(parentComment.get())
                .build();

        commentRepository.save(comment);

        Activity activity = Activity.builder()
                .post(post.get())
                .comment(comment)
                .type(ActivityType.COMMENT)
                .user(user)
                .build();

        activityRepository.save(activity);
    }


    @Override
    @Transactional
    public void updateComment(Long id, CommentRequestDto dto) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND_ERROR, "존재하지 않는 댓글입니다.");
        }

        User user = SecurityUtil.getUser();

        Comment comment = optionalComment.get();
        Optional<Post> optionalPost = postRepository.findById(comment.getPost().getId());
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        String currentUserId = user.getUserid();
        if (!comment.getUser().getUserid().equals(currentUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(dto.getContent());
        comment.setSecret(dto.isSecret());
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND_ERROR, "존재하지 않는 댓글입니다.");
        }

        User user = SecurityUtil.getUser();
        Comment comment = optionalComment.get();
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

        /**
         * 삭제하려는 댓글의 자식 댓글이 있는 경우
         * db에서 삭제가 아닌 '삭제된 댓글입니다.'로 표시하기 위해 isRemoved = true로 변경
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
}
