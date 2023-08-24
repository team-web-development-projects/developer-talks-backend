package com.dtalks.dtalks.admin.visitor.controller;

import com.dtalks.dtalks.admin.visitor.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/visitors")
public class VisitorController {
    private final VisitorService visitorService;

    @Operation(summary = "특정 기간 일일 방문자수 조회", parameters = {
            @Parameter(name = "startDate", description = "조회 시작 날짜 0000-00-00 형태"),
            @Parameter(name = "endDate", description = "조회 끝 날짜 0000-00-00 형태")
    })
    @GetMapping("/daily-count")
    public ResponseEntity<Map<LocalDate, Integer>> getDailyVisitorCounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<LocalDate, Integer> dailyCounts = visitorService.getDailyVisitorCounts(startDate, endDate);
        return ResponseEntity.ok(dailyCounts);
    }

    @Operation(summary = "일일 방문자수 증가, 같은 ip는 해당 날짜 최초방문시에만 count 증가")
    @PostMapping("/increase")
    public ResponseEntity<Void> increaseVisitorCount(HttpServletRequest request) {
        visitorService.increaseVisitorCount(request);
        return ResponseEntity.ok().build();
    }
}
