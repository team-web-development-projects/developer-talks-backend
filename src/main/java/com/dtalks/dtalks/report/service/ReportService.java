package com.dtalks.dtalks.report.service;

import com.dtalks.dtalks.report.dto.ReportDetailRequestDto;

public interface ReportService {
    void reportUser(String nickname, ReportDetailRequestDto dto);
    void reportPost(Long id, ReportDetailRequestDto dto);
}
