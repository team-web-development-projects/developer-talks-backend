package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoritePostService {

    Integer favorite(Long postId);
    Integer unFavorite(Long postId);

    Page<PostDto> searchFavoritePostsByUser(String nickname, Pageable pageable);

    boolean checkFavorite(Long postId);
}
