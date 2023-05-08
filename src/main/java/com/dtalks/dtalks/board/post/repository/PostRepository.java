package com.dtalks.dtalks.board.post.repository;

import com.dtalks.dtalks.board.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByIdDesc(Pageable pageable);
    List<Post> findByUserId(Long id);
}
