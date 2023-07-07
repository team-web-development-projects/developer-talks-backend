package com.dtalks.dtalks.board.post.repository;

import com.dtalks.dtalks.board.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long id, Pageable pageable);
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);
    List<Post> findByUserIdAndCreateDateBetween(Long userId, LocalDateTime goe, LocalDateTime loe);
    List<Post> findTop5ByCreateDateGreaterThanEqualAndRecommendCountGreaterThanOrderByRecommendCountDesc(LocalDateTime goe, int cnt);
}
