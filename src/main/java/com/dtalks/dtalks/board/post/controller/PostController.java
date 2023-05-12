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

    @Operation(summary = "특정 게시글 id로 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.searchById(id));
    }

    @Operation(summary = "모든 게시글 조회 (페이지 사용)")
    @GetMapping("/all")
    public ResponseEntity<Page<PostDto>> searchAll(@PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.searchAllPost(pageable));
    }

    @Operation(summary = "특정 유저의 게시글 조회 (페이지 사용 - 기본 post id로 정렬)")
    @GetMapping("/list/user/{id}")
    public ResponseEntity<Page<PostDto>> searchPostsByUser(@PathVariable Long id,
                                                           @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.searchPostsByUser(id, pageable));
    }

    @Operation(summary = "검색어로 게시글 검색")
    @GetMapping("/search")
    public ResponseEntity<Page<PostDto>> searchPosts(@RequestParam String keyword,
                                                    @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.searchByWord(keyword, pageable));
    }

    @Operation(summary = "조회수 베스트 5 게시글 가져오기 (리스트)")
    @GetMapping("/best")
    public ResponseEntity<List<PostDto>> search5BestPosts() {
        return ResponseEntity.ok(postService.search5BestPosts());
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
