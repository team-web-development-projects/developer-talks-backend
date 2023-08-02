package com.dtalks.dtalks.report.controller;

import com.dtalks.dtalks.report.dto.ReportDetailRequestDto;
import com.dtalks.dtalks.report.service.ReportUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportUserService reportUserService;

    @Operation(summary = "사용자 신고", parameters = {
            @Parameter(name = "nickname", description = "조회할 유저의 nickname"),
            @Parameter(name = "dto", description = "신고 타입과 상세 내역")
    })
    @PostMapping("/{nickname}")
    public ResponseEntity<Void> submitUserReport(@PathVariable String nickname, @RequestBody ReportDetailRequestDto dto) {
        reportUserService.report(nickname, dto);
        return ResponseEntity.ok().build();
    }
}
