package com.dtalks.dtalks.admin.post.controller;

import com.dtalks.dtalks.admin.post.dto.AdminPostDto;
import com.dtalks.dtalks.admin.post.service.AdminPostService;
import com.dtalks.dtalks.exception.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
            description = "파라미터 없으면 foribdden에 상관 없이 전체 조회, forbidden=true면 접근 금지 처리 된 글 조회, false는 금지 처리 되지 않은 게시글 조회.")
    @GetMapping("/all")
    public ResponseEntity<Page<AdminPostDto>> getAllPosts(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Boolean forbidden) {
        return ResponseEntity.ok(adminPostService.getAllPosts(pageable, forbidden));
    }

    @Operation(summary = "게시글 접근금지 처리 (forbidden이 true로 바뀜, 사용자 접근 불가능으로 바뀜)", responses = {
            @ApiResponse(responseCode = "404", description = "해당 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/forbid")
    public ResponseEntity<Void> forbidPost(@RequestParam Long id) {
        adminPostService.forbidPost(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 복구 처리 (forbidden이 false로 바뀜, 사용자 접근 가능해짐)", responses = {
            @ApiResponse(responseCode = "400", description = "접근 금지 게시글이 아님", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/restore")
    public ResponseEntity<Void> restorePost(@RequestParam Long id) {
        adminPostService.restorePost(id);
        return ResponseEntity.ok().build();
    }
}
