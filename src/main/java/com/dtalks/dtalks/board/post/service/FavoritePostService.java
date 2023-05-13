package com.dtalks.dtalks.board.post.service;

public interface FavoritePostService {

    void favorite(Long postId);
    void unFavorite(Long postId);

    boolean checkFavorite(Long postId);
}
