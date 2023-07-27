package com.dtalks.dtalks.report.dto;

import com.dtalks.dtalks.report.enums.ReportType;
import lombok.Getter;

@Getter
public class ReportDetailRequestDto {
    private ReportType reportType;
    private String detail;
}
