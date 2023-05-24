package com.dtalks.dtalks.board.comment.repository;

import com.dtalks.dtalks.board.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByUserIdAndIsRemovedFalse(Long userId);
}
