package com.dtalks.dtalks.report.dto;

import com.dtalks.dtalks.report.entity.ReportedUser;
import com.dtalks.dtalks.report.enums.ReportType;
import com.dtalks.dtalks.report.enums.ResultType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDetailDto {
    private Long id;
    private String reportedUserNickname;

    private ReportType reportType;
    private String detail;

    private boolean processed;
    private ResultType resultType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public static ReportDetailDto toDto(ReportedUser report) {
        return ReportDetailDto.builder()
                .id(report.getId())
                .reportedUserNickname(report.getReportedUser().getNickname())
                .reportType(report.getReportType())
                .detail(report.getDetail())
                .processed(report.isProcessed())
                .resultType(report.getResultType())
                .createDate(report.getCreateDate())
                .modifiedDate(report.getModifiedDate())
                .build();
    }
}
