package com.dtalks.dtalks.admin.report.controller;

import com.dtalks.dtalks.admin.report.dto.ReportDetailDto;
import com.dtalks.dtalks.admin.report.dto.ReportDetailRequestDto;
import com.dtalks.dtalks.admin.report.service.ReportUserService;
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


    @GetMapping("/user")
    public ResponseEntity<Page<ReportDetailDto>> searchAllUserReports(@PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reportUserService.searchAllUserReports(pageable));
    }

    @PostMapping("/{nickname}")
    public ResponseEntity<Void> submitUserReport(@PathVariable String nickname, @RequestBody ReportDetailRequestDto dto) {
        reportUserService.report(nickname, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelUserReport(@PathVariable Long id) {
        reportUserService.cancelReport(id);
        return ResponseEntity.ok().build();
    }
}
