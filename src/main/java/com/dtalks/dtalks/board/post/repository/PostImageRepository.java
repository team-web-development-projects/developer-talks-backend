package com.dtalks.dtalks.board.post.repository;

import com.dtalks.dtalks.board.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);
    List<PostImage> findByPostIdOrderByOrderNum(Long postId);
    Optional<PostImage> findTop1ByPostId(Long postId);
}
