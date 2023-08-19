package com.dtalks.dtalks.admin.post.service;

import com.dtalks.dtalks.admin.post.dto.AdminPostDto;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPostServiceImpl implements AdminPostService {

    private final PostRepository postRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminPostDto> getAllPosts(Pageable pageable, boolean removed) {
        Page<Post> posts;
        if (removed == true) {
            posts = postRepository.findByForbiddenFalse(pageable);
        } else {
            posts = postRepository.findByForbiddenTrue(pageable);
        }
        return posts.map(AdminPostDto::toDto);
    }

    @Override
    @Transactional
    public void forbidPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 게시글을 찾을 수 없습니다."));
        User user = post.getUser();
        post.forbid();
        if (user.getUserid() != null) {
            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(null, null, user, NotificationType.POST_FORBIDDEN,
                    messageSource.getMessage("notification.admin.post.forbidden", new Object[]{post.getTitle()}, null)));
        }
    }

    @Override
    @Transactional
    public void restorePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 게시글을 찾을 수 없습니다."));
        if (!post.isForbidden()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "접근 금지 처리된 게시글이 아닙니다.");
        }

        User user = post.getUser();
        post.restore();
        if (user.getUserid() != null) {
            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(null, null, user, NotificationType.POST_RESTORED,
                    messageSource.getMessage("notification.admin.post.restore", new Object[]{post.getTitle()}, null)));
        }
    }
}
