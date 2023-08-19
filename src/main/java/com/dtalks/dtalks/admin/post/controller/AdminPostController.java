package com.dtalks.dtalks.admin.post.controller;

import com.dtalks.dtalks.admin.post.dto.AdminPostDto;
import com.dtalks.dtalks.admin.post.service.AdminPostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/posts")
public class AdminPostController {
    private final AdminPostService adminPostService;

    @Operation(summary = "게시글들 조회 (전체 조회랑 마찬가지) (size=10, sort=id,desc 적용)",
            description = "파라미터 없으면 관리자에 의해 접근금지 처리 되지 않은 글 조회, removed=true면 접근금지 처리 된 글 조회.")
    @GetMapping("/all")
    public ResponseEntity<Page<AdminPostDto>> getAllPosts(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) boolean removed) {
        return ResponseEntity.ok(adminPostService.getAllPosts(pageable, removed));
    }

    @Operation(summary = "게시글 접근금지 처리 (forbidden이 true로 바뀜, 사용자 접근 불가능으로 바뀜)")
    @PutMapping("/forbid/{id}")
    public ResponseEntity<Void> forbidPost(@PathVariable Long id) {
        adminPostService.forbidPost(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 복구 처리 (forbidden이 false로 바뀜, 사용자 접근 가능해짐)")
    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restorePost(@PathVariable Long id) {
        adminPostService.restorePost(id);
        return ResponseEntity.ok().build();
    }
}
