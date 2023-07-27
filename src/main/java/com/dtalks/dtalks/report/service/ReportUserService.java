package com.dtalks.dtalks.report.service;

import com.dtalks.dtalks.report.dto.ReportDetailDto;
import com.dtalks.dtalks.report.dto.ReportDetailRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportUserService {
    Page<ReportDetailDto> searchAllUserReports(Pageable pageable);

    void report(String nickname, ReportDetailRequestDto dto);
    void cancelReport(Long id);

}
