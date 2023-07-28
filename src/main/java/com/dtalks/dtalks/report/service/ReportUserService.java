package com.dtalks.dtalks.report.service;

import com.dtalks.dtalks.report.dto.ReportDetailRequestDto;

public interface ReportUserService {
    void report(String nickname, ReportDetailRequestDto dto);
}
