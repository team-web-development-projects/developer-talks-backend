package com.dtalks.dtalks.admin.post.controller;

import com.dtalks.dtalks.admin.post.dto.AdminPostDto;
import com.dtalks.dtalks.admin.post.service.AdminPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/post")
public class AdminPostController {
    private final AdminPostService adminPostService;

    @GetMapping("/all")
    public ResponseEntity<Page<AdminPostDto>> getAllPosts(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminPostService.getAllPosts(pageable));
    }

    @GetMapping("/all/removed")
    public ResponseEntity<Page<AdminPostDto>> getAllRemovedPosts(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminPostService.getAllRemovedPosts(pageable));
    }

    @PutMapping("/remove/{id}")
    public ResponseEntity<Void> removePost(@PathVariable Long id) {
        adminPostService.removePost(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restorePost(@PathVariable Long id) {
        adminPostService.restorePost(id);
        return ResponseEntity.ok().build();
    }
}
