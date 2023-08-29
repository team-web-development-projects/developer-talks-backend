package com.dtalks.dtalks.report.controller;

import com.dtalks.dtalks.exception.dto.ErrorResponseDto;
import com.dtalks.dtalks.report.dto.ReportDetailRequestDto;
import com.dtalks.dtalks.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "사용자 신고", parameters = {
            @Parameter(name = "nickname", description = "조회할 유저의 nickname"),
            @Parameter(name = "dto", description = "신고 타입과 상세 내역")
    }, responses = {
            @ApiResponse(responseCode = "202", description = "탈퇴한 사용자는 신고 불가 / 신고로 인해 이미 정지된 계정 / 이미 해당 사용자를 신고한 관리자가 처리하지 않은 기록이 있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "해당 사용자가 db에 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/user")
    public ResponseEntity<Void> submitUserReport(@RequestParam String nickname, @RequestBody @Valid ReportDetailRequestDto dto) {
        reportService.reportUser(nickname, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 신고", responses = {
            @ApiResponse(responseCode = "202", description = "이미 해당 게시글을 신고한 관리자가 처리하지 않은 기록이 있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 게시글이 db에 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/post")
    public ResponseEntity<Void> submitPostReport(@RequestParam Long id, @RequestBody @Valid ReportDetailRequestDto dto) {
        reportService.reportPost(id, dto);
        return ResponseEntity.ok().build();
    }
}
