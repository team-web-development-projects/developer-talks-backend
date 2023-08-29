package com.dtalks.dtalks.admin.report.controller;

import com.dtalks.dtalks.admin.report.dto.ReportDetailDto;
import com.dtalks.dtalks.admin.report.dto.ReportedPostDto;
import com.dtalks.dtalks.admin.report.dto.ReportedUserDto;
import com.dtalks.dtalks.admin.report.enums.DType;
import com.dtalks.dtalks.admin.report.service.AdminReportService;
import com.dtalks.dtalks.exception.dto.ErrorResponseDto;
import com.dtalks.dtalks.report.enums.ResultType;
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
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @Operation(summary = "신고가 들어온 사용자들 페이지로 보기 (size=10, sort=id,desc 적용)")
    @GetMapping("/user/all")
    public ResponseEntity<Page<ReportedUserDto>> searchAllNotProgressedUserReports(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminReportService.searchAllNotProgressedUserReports(pageable));
    }

    @Operation(summary = "신고된 사용자에게 해당하는 신고 내역 보기 (size=10, sort=id,desc 적용)")
    @GetMapping("/user")
    public ResponseEntity<Page<ReportDetailDto>> getAllNotProgressedReportsByUser(
            @RequestParam Long reportedUserId,
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminReportService.getAllNotProgressedReportsByUser(reportedUserId, pageable));
    }

    @Operation(summary = "신고된 게시글들 보기")
    @GetMapping("/post/all")
    public ResponseEntity<Page<ReportedPostDto>> searchAllNotProgressedPostReports(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminReportService.searchAllNotProgressedPostReports(pageable));
    }

    @Operation(summary = "신고된 게시글에게 해당하는 신고 내역 보기 (size=10, sort=id,desc 적용)")
    @GetMapping("/post")
    public ResponseEntity<Page<ReportDetailDto>> getAllNotProgressedReportsByPost(
            @RequestParam Long reportedPostId,
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminReportService.getAllNotProgressedReportsByPost(reportedPostId, pageable));
    }

    @Operation(summary = "신고된 사용자 또는 게시글 처리", description = "사용자일 경우에는 BAN, SUSPENSION, NP / 게시글은 NP, FORBIDDEN", responses = {
            @ApiResponse(responseCode = "400", description = "USER(NP,SUSPENSION,BAN)나 POST(NP,FORBIDDEN)에 처리할 수 있는 타입이 다른데 잘못 들어올때 발생하는 에러", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 신고 내역이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/handle/{dtype}")
    public ResponseEntity<ResultType> handleReports(@PathVariable DType dtype, @RequestParam Long reportedObjectId, @RequestParam ResultType resultType) {
        return ResponseEntity.ok(adminReportService.handleReports(dtype, reportedObjectId, resultType));
    }
}
