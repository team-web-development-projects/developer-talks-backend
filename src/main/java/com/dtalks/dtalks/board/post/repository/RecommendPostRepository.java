package com.dtalks.dtalks.board.post.repository;

import com.dtalks.dtalks.board.post.entity.RecommendPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendPostRepository extends JpaRepository<RecommendPost, Long> {
    Optional<RecommendPost> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
