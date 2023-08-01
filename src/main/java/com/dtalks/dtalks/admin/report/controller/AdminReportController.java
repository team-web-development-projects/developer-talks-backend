package com.dtalks.dtalks.admin.report.controller;

import com.dtalks.dtalks.admin.report.dto.ReportDetailDto;
import com.dtalks.dtalks.admin.report.dto.ReportedUserDto;
import com.dtalks.dtalks.admin.report.service.AdminReportService;
import com.dtalks.dtalks.report.enums.ResultType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/report")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @Operation(summary = "신고가 들어온 사용자들 페이지로 보기 (size=10, sort=id,desc 적용)")
    @GetMapping("/all")
    public ResponseEntity<Page<ReportedUserDto>> searchUserReports(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminReportService.searchAllUserReports(pageable));
    }

    @Operation(summary = "신고된 사용자에게 해당하는 신고 내역 보기 (size=10, sort=id,desc 적용)")
    @GetMapping("/user/{reportedUserId}")
    public ResponseEntity<Page<ReportDetailDto>> getAllReportsByUser(
            @PathVariable Long reportedUserId,
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminReportService.getAllReportsByUser(reportedUserId, pageable));
    }

    @Operation(summary = "신고 처리, resultType=\"BAN / SUSPENSION / NP\"")
    @PutMapping("/user/{reportedUserId}/{resultType}")
    public ResponseEntity<ResultType> handleReport(@PathVariable Long reportedUserId, @PathVariable ResultType resultType) {
        return ResponseEntity.ok(adminReportService.handleReport(reportedUserId, resultType));
    }
}
