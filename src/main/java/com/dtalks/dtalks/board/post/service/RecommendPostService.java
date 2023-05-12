package com.dtalks.dtalks.board.post.service;

public interface RecommendPostService {
    void recommend(Long postId);
    void cancelRecommend(Long postId);

    boolean checkRecommend(Long postId);
}
