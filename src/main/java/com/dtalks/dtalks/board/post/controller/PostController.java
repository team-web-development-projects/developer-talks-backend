package com.dtalks.dtalks.board.post.controller;

import com.dtalks.dtalks.board.post.service.PostService;
import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "특정 게시글 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.searchById(id));
    }

    @Operation(summary = "모든 게시글 조회 (페이지 사용)")
    @GetMapping("/all")
    public ResponseEntity<Page<PostDto>> searchAll(@PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.searchAllPost(pageable));
    }

    @Operation(summary = "특정 유저의 게시글 리스트 조회")
    @GetMapping("/list/user/{id}")
    public ResponseEntity<List<PostDto>> searchPostListByUser(@PathVariable Long id) {
        return ResponseEntity.ok(postService.searchPostListByUser(id));
    }

    @Operation(summary = "게시글 생성")
    @PostMapping
    public ResponseEntity<Long> createPost(@Valid @RequestBody PostRequestDto postDto) {
        return ResponseEntity.ok(postService.createPost(postDto));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePost(@Valid @RequestBody PostRequestDto postDto, @PathVariable Long id) {
        return ResponseEntity.ok(postService.updatePost(postDto, id));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @Operation(summary = "게시글 조회수 업데이트 (+1)")
    @PutMapping("/view/{id}")
    public void updateViewCount(@PathVariable Long id) {
        postService.updateViewCount(id);
    }
}
