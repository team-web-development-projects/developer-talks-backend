package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.entity.RecommendPost;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.board.post.repository.RecommendPostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.*;
import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.entity.Notification;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.repository.NotificationRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendPostServiceImpl implements RecommendPostService {

    private final RecommendPostRepository recommendPostRepository;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final MessageSource messageSource;

    @Override
    @Transactional
    public Integer recommend(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다."));
        User user = SecurityUtil.getUser();

        User postWriter = post.getUser();
        if (user.getId() == postWriter.getId()) {
            throw new CustomException(ErrorCode.ACCEPTED_BUT_IMPOSSIBLE, "작성한 글에는 추천이 불가능합니다.");
        }
        
        if (recommendPostRepository.findByPostIdAndUserId(postId, user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_ERROR, "이미 추천한 게시글입니다.");
        }

        RecommendPost recommendPost = RecommendPost.builder().post(post).user(user).build();
        recommendPostRepository.save(recommendPost);

        post.plusRecommentCount();

        if (postWriter.getIsActive()) {
            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(recommendPost.getId(), post.getId(), postWriter,
                    NotificationType.RECOMMEND_POST, messageSource.getMessage("notification.post.recommend", new Object[]{post.getTitle()}, null)));
        }
         return post.getRecommendCount();
    }

    @Override
    @Transactional
    public Integer cancelRecommend(Long postId) {
        User user = SecurityUtil.getUser();

        RecommendPost recommendPost = recommendPostRepository.findByPostIdAndUserId(postId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.FAVORITE_POST_NOT_FOUND_ERROR, "해당 게시글은 추천 상태가 아닙니다."));
        recommendPostRepository.delete(recommendPost);

        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다."));
        post.minusRecommentCount();

        if (post.getUser().getIsActive()) {
            Notification notification = notificationRepository.findByRefIdAndType(recommendPost.getId(), NotificationType.RECOMMEND_POST)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND_ERROR, "해당하는 알림이 존재하지 않습니다."));
            if (notification.getReadStatus().equals(ReadStatus.READ)) {
                notification.readDataDeleteSetting();
            } else {
                notificationRepository.deleteById(notification.getId());
            }
        }

        return post.getRecommendCount();
    }

    @Override
    public boolean checkRecommend(Long postId) {
        User user = SecurityUtil.getUser();
        return recommendPostRepository.existsByPostIdAndUserId(postId, user.getId());
    }
}
