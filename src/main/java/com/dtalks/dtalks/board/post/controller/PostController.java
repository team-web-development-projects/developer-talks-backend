package com.dtalks.dtalks.board.post.controller;

import com.dtalks.dtalks.board.post.service.PostService;
import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.searchById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PostDto>> searchAll(@PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.searchAllPost(pageable));
    }

    @PostMapping
    public ResponseEntity<Long> createPost(@Valid @RequestBody PostRequestDto postDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.createPost(postDto, userDetails));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePost(@Valid @RequestBody PostRequestDto postDto, @PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(postService.updatePost(postDto, id, user));
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        postService.deletePost(id, user);
    }
}
