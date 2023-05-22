package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.entity.RecommendPost;
import com.dtalks.dtalks.board.post.repository.CustomPostRepository;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.board.post.repository.RecommendPostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.*;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendPostServiceImpl implements RecommendPostService {

    private final RecommendPostRepository recommendPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CustomPostRepository customPostRepository;

    @Override
    @Transactional
    public void recommend(Long postId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        Post post = optionalPost.get();
        User user = optionalUser.get();

        if (user == post.getUser()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "작성한 글에는 추천이 불가능합니다.");
        }
        
        if (recommendPostRepository.findByPostIdAndUserId(postId, user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_ERROR, "이미 추천한 게시글입니다.");
        }

        RecommendPost recommendPost = RecommendPost.toEntity(post, user);
        recommendPostRepository.save(recommendPost);

        customPostRepository.updateRecommendCount(post, true);
    }

    @Override
    @Transactional
    public void cancelRecommend(Long postId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        User user = optionalUser.get();
        Optional<RecommendPost> optionalRecommendPost = recommendPostRepository.findByPostIdAndUserId(postId, user.getId());
        if (optionalRecommendPost.isEmpty()) {
            throw new CustomException(ErrorCode.FAVORITE_POST_NOT_FOUND_ERROR, "해당 게시글은 추천 상태가 아닙니다.");
        }

        RecommendPost recommendPost = optionalRecommendPost.get();
        recommendPostRepository.delete(recommendPost);

        Post post = optionalPost.get();
        customPostRepository.updateRecommendCount(post, false);
    }

    @Override
    public boolean checkRecommend(Long postId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();
        return recommendPostRepository.existsByPostIdAndUserId(postId, user.getId());
    }
}
