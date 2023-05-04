package com.dtalks.dtalks.board.comment.controller;

import com.dtalks.dtalks.board.comment.dto.CommentInfoDto;
import com.dtalks.dtalks.board.comment.dto.CommentRequestDto;
import com.dtalks.dtalks.board.comment.service.CommentService;
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

    @GetMapping("/{id}")
    public ResponseEntity<CommentInfoDto> searchOneComment(@PathVariable Long id) {
        CommentInfoDto commentInfoDto = commentService.searchById(id);
        return ResponseEntity.ok(commentInfoDto);
    }

    @GetMapping("/list/post/{postId}")
    public ResponseEntity<List<CommentInfoDto>> searchPostCommentList(@PathVariable Long postId) {
        List<CommentInfoDto> list = commentService.searchListByPostId(postId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/list/user/{userId}")
    public ResponseEntity<List<CommentInfoDto>> searchUserIdCommentList(@PathVariable Long userId) {
        List<CommentInfoDto> list = commentService.searchListByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{postId}")
    public void saveComment(@PathVariable Long postId, @Valid @RequestBody CommentRequestDto dto) {
        commentService.saveComment(postId, dto);
    }

    @PostMapping("/{postId}/{parentId}")
    public void saveReComment(@PathVariable Long postId, @PathVariable Long parentId, @Valid @RequestBody CommentRequestDto dto) {
        commentService.saveReComment(postId, parentId, dto);
    }

    @PutMapping("/{postId}")
    public void updateComment(@PathVariable Long postId, @Valid @RequestBody CommentRequestDto dto) {
        commentService.updateComment(postId, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
