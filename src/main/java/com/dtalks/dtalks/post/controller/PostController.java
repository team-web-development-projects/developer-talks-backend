package com.dtalks.dtalks.post.controller;

import com.dtalks.dtalks.post.dto.PostRequestDto;
import com.dtalks.dtalks.post.dto.PostResponseDto;
import com.dtalks.dtalks.post.service.PostService;
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
    public ResponseEntity<PostResponseDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.searchById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PostResponseDto>> searchAll(@PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
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
