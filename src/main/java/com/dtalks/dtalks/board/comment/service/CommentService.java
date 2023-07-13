package com.dtalks.dtalks.board.comment.service;

import com.dtalks.dtalks.board.comment.dto.CommentInfoDto;
import com.dtalks.dtalks.board.comment.dto.CommentRequestDto;
import com.dtalks.dtalks.board.comment.dto.UserCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentInfoDto searchById(Long id);
    List<CommentInfoDto> searchListByPostId(Long postId);
    Page<UserCommentDto> searchListByNickname(String nickname, Pageable pageable);

    void saveComment(Long postId, CommentRequestDto dto);
    void saveReComment(Long postId, Long parentId, CommentRequestDto dto);

    void updateComment(Long id, CommentRequestDto dto);
    void deleteComment(Long id);
}
