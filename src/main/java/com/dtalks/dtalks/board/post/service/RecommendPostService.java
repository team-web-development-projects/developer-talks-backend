package com.dtalks.dtalks.board.post.service;

public interface RecommendPostService {
    Integer recommend(Long postId);
    Integer cancelRecommend(Long postId);

    boolean checkRecommend(Long postId);
}
