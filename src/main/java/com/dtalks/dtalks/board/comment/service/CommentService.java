package com.dtalks.dtalks.board.comment.service;

import com.dtalks.dtalks.board.comment.dto.CommentInfoDto;
import com.dtalks.dtalks.board.comment.dto.CommentRequestDto;

import java.util.List;

public interface CommentService {
    CommentInfoDto searchById(Long id);
    List<CommentInfoDto> searchListByPostId(Long postId);
    List<CommentInfoDto> searchListByUserId(String userId);

    void saveComment(Long postId, CommentRequestDto dto);
    void saveReComment(Long postId, Long parentId, CommentRequestDto dto);

    void updateComment(Long id, CommentRequestDto dto);
    void deleteComment(Long id);
}
