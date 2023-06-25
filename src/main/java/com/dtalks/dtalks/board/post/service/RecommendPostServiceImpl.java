package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.alarm.entity.Alarm;
import com.dtalks.dtalks.alarm.enums.AlarmType;
import com.dtalks.dtalks.alarm.repository.AlarmRepository;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.entity.RecommendPost;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.board.post.repository.RecommendPostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.*;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendPostServiceImpl implements RecommendPostService {

    private final RecommendPostRepository recommendPostRepository;
    private final PostRepository postRepository;
    private final AlarmRepository alarmRepository;

    @Override
    @Transactional
    public Integer recommend(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        Post post = optionalPost.get();
        User user = SecurityUtil.getUser();

        if (user == post.getUser()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "작성한 글에는 추천이 불가능합니다.");
        }
        
        if (recommendPostRepository.findByPostIdAndUserId(postId, user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_ERROR, "이미 추천한 게시글입니다.");
        }

        RecommendPost recommendPost = RecommendPost.toEntity(post, user);
        recommendPostRepository.save(recommendPost);

        post.setRecommendCount(post.getRecommendCount() + 1);

        alarmRepository.save(Alarm.createAlarm(post.getUser(), AlarmType.RECOMMEND_POST, "작성한 게시글이 추천되었습니다.", "/post/" + postId));

        return post.getRecommendCount();
    }

    @Override
    @Transactional
    public Integer cancelRecommend(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        User user = SecurityUtil.getUser();
        Optional<RecommendPost> optionalRecommendPost = recommendPostRepository.findByPostIdAndUserId(postId, user.getId());
        if (optionalRecommendPost.isEmpty()) {
            throw new CustomException(ErrorCode.FAVORITE_POST_NOT_FOUND_ERROR, "해당 게시글은 추천 상태가 아닙니다.");
        }

        RecommendPost recommendPost = optionalRecommendPost.get();
        recommendPostRepository.delete(recommendPost);

        Post post = optionalPost.get();
        post.setRecommendCount(post.getRecommendCount() - 1);
        return post.getRecommendCount();
    }

    @Override
    public boolean checkRecommend(Long postId) {
        User user = SecurityUtil.getUser();
        return recommendPostRepository.existsByPostIdAndUserId(postId, user.getId());
    }
}
