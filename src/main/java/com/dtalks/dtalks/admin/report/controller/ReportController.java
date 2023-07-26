package com.dtalks.dtalks.admin.report.controller;

import com.dtalks.dtalks.admin.report.dto.ReportDetailDto;
import com.dtalks.dtalks.admin.report.dto.ReportDetailRequestDto;
import com.dtalks.dtalks.admin.report.service.ReportUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportUserService reportUserService;


    @Operation(summary = "자신이 신고한 사용자 보기 (페이지 사용, size = 10, sort=\"report id\" desc 적용)")
    @GetMapping("/user")
    public ResponseEntity<Page<ReportDetailDto>> searchAllUserReports(@PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reportUserService.searchAllUserReports(pageable));
    }

    @Operation(summary = "사용자 신고", parameters = {
            @Parameter(name = "nickname", description = "조회할 유저의 nickname"),
            @Parameter(name = "dto", description = "신고 타입과 상세 내역")
    })
    @PostMapping("/{nickname}")
    public ResponseEntity<Void> submitUserReport(@PathVariable String nickname, @RequestBody ReportDetailRequestDto dto) {
        reportUserService.report(nickname, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 신고 취소", parameters = {
            @Parameter(name = "id", description = "신고 report id (primary key)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelUserReport(@PathVariable Long id) {
        reportUserService.cancelReport(id);
        return ResponseEntity.ok().build();
    }
}
