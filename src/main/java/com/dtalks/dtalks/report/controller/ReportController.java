package com.dtalks.dtalks.report.controller;

import com.dtalks.dtalks.report.dto.ReportDetailRequestDto;
import com.dtalks.dtalks.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    })
    @PostMapping("/user")
    public ResponseEntity<Void> submitUserReport(@RequestParam String nickname, @RequestBody @Valid ReportDetailRequestDto dto) {
        reportService.reportUser(nickname, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 신고")
    @PostMapping("/post")
    public ResponseEntity<Void> submitPostReport(@RequestParam Long id, @RequestBody @Valid ReportDetailRequestDto dto) {
        reportService.reportPost(id, dto);
        return ResponseEntity.ok().build();
    }
}
