package com.dtalks.dtalks.board.post.repository;

import com.dtalks.dtalks.board.post.entity.FavoritePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {
    Optional<FavoritePost> findByPostIdAndUserId(Long postId, Long userId);
    Page<FavoritePost> findByUserId(Long userId, Pageable pageable);
    List<FavoritePost> findByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
