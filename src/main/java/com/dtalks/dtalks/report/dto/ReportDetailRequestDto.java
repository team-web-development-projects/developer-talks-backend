package com.dtalks.dtalks.report.dto;

import com.dtalks.dtalks.report.enums.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "사용자의 신고 dto")
public class ReportDetailRequestDto {

    @Schema(description = "신고 타입: SWEAR_WORD, OTHER. 필수")
    @NotBlank
    private ReportType reportType;

    @Schema(description = "신고 상세 내역으로 작성하지 않아도 됨. 선택")
    private String detail;
}
