package com.dtalks.dtalks.admin.report.service;

import com.dtalks.dtalks.admin.report.dto.ReportDetailDto;
import com.dtalks.dtalks.admin.report.dto.ReportedPostDto;
import com.dtalks.dtalks.admin.report.dto.ReportedUserDto;
import com.dtalks.dtalks.admin.report.enums.DType;
import com.dtalks.dtalks.report.enums.ResultType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReportService {
    Page<ReportedUserDto> searchAllNotProgressedUserReports(Pageable pageable);
    Page<ReportDetailDto> getAllNotProgressedReportsByUser(Long reportedUserId, Pageable pageable);

    Page<ReportedPostDto> searchAllNotProgressedPostReports(Pageable pageable);
    Page<ReportDetailDto> getAllNotProgressedReportsByPost(Long reportedPostId, Pageable pageable);

    ResultType handleReports(DType dType, Long reportedObjectId, ResultType resultType);
}
