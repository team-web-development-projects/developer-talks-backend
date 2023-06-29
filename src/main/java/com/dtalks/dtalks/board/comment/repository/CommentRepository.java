package com.dtalks.dtalks.board.comment.repository;

import com.dtalks.dtalks.board.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByPostId(Long postId);
    List<Comment> findByPostId(Long postId);
    List<Comment> findByUserIdAndRemovedFalse(Long userId);
}
