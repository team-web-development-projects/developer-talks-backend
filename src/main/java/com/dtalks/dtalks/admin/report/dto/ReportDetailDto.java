package com.dtalks.dtalks.admin.report.dto;

import com.dtalks.dtalks.report.entity.ReportedUser;
import com.dtalks.dtalks.report.enums.ReportType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "관리자가 보는 신고 상세 내역 DTO")
public class ReportDetailDto {
    private Long id;

    @Schema(description = "신고한 사용자의 닉네임")
    private String reportUserNickname;

    @Schema(description ="신고 타입: SWEAR_WORD, OTHER")
    private ReportType reportType;

    @Schema(description ="신고 상세 내역(선택사항이어서 비어있을 수 있음)")
    private String detail;

    @Schema(description ="확인 여부 - 기본 false (관리자가 확인했으면 true)")
    private boolean processed;

    @Schema(description ="신고 접수 date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public static ReportDetailDto toDto(ReportedUser report) {
        return ReportDetailDto.builder()
                .id(report.getId())
                .reportUserNickname(report.getReportUser().getNickname())
                .reportType(report.getReportType())
                .detail(report.getDetail())
                .processed(report.isProcessed())
                .createDate(report.getCreateDate())
                .modifiedDate(report.getModifiedDate())
                .build();
    }
}
