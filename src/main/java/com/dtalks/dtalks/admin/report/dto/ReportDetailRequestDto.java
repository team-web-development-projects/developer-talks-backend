package com.dtalks.dtalks.admin.report.dto;

import com.dtalks.dtalks.admin.report.enums.ReportType;
import lombok.Getter;

@Getter
public class ReportDetailRequestDto {
    private ReportType reportType;
    private String detail;
}
