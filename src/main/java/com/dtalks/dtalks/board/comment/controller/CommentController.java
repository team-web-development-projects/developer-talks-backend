package com.dtalks.dtalks.board.comment.controller;

import com.dtalks.dtalks.board.comment.dto.CommentInfoDto;
import com.dtalks.dtalks.board.comment.dto.CommentRequestDto;
import com.dtalks.dtalks.board.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "특정 댓글 조회")
    @GetMapping("/{id}")
    public ResponseEntity<CommentInfoDto> searchOneComment(@PathVariable Long id) {
        CommentInfoDto commentInfoDto = commentService.searchById(id);
        return ResponseEntity.ok(commentInfoDto);
    }

    @Operation(summary = "특정 게시글의 댓글 리스트 조회")
    @GetMapping("/list/post/{postId}")
    public ResponseEntity<List<CommentInfoDto>> searchPostCommentList(@PathVariable Long postId) {
        List<CommentInfoDto> list = commentService.searchListByPostId(postId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "특정 유저의 댓글 리스트 조회")
    @GetMapping("/list/user/{userId}")
    public ResponseEntity<List<CommentInfoDto>> searchUserIdCommentList(@PathVariable Long userId) {
        List<CommentInfoDto> list = commentService.searchListByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "특정 게시글에 댓글 저장")
    @PostMapping("/{postId}")
    public void saveComment(@PathVariable Long postId, @Valid @RequestBody CommentRequestDto dto) {
        commentService.saveComment(postId, dto);
    }

    @Operation(summary = "특정 게시글의 댓글에 대댓글 저장")
    @PostMapping("/{postId}/{parentId}")
    public void saveReComment(@PathVariable Long postId, @PathVariable Long parentId, @Valid @RequestBody CommentRequestDto dto) {
        commentService.saveReComment(postId, parentId, dto);
    }

    @Operation(summary = "특정 댓글 수정")
    @PutMapping("/{id}")
    public void updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequestDto dto) {
        commentService.updateComment(id, dto);
    }

    @Operation(summary = "특정 댓글 삭제")
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
