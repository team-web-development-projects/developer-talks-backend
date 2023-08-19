package com.dtalks.dtalks.admin.report.service;

import com.dtalks.dtalks.admin.report.dto.ReportDetailDto;
import com.dtalks.dtalks.admin.report.dto.ReportedUserDto;
import com.dtalks.dtalks.report.enums.ResultType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReportService {
    Page<ReportedUserDto> searchAllNotProgressedUserReports(Pageable pageable);
    Page<ReportDetailDto> getAllNotProgressedReportsByUser(Long reportedUserId, Pageable pageable);
    ResultType handleReport(Long reportedUserId, ResultType resultType);
}
