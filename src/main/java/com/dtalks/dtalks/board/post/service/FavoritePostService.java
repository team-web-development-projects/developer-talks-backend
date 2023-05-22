package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoritePostService {

    void favorite(Long postId);
    void unFavorite(Long postId);

    Page<PostDto> searchFavoritePostsByUser(String userId, Pageable pageable);

    boolean checkFavorite(Long postId);
}
