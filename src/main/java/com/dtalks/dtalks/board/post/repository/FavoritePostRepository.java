package com.dtalks.dtalks.board.post.repository;

import com.dtalks.dtalks.board.post.entity.FavoritePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {
    Optional<FavoritePost> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
