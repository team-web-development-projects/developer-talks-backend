package com.dtalks.dtalks.admin.report.dto;

import com.dtalks.dtalks.admin.user.dto.UserManageDto;
import com.dtalks.dtalks.report.enums.ResultType;
import com.dtalks.dtalks.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "신고 당한 사용자의 정보와 처리 내용")
public class ReportedUserDto {
    @Schema(description = "신고 당한 사용자 정보")
    private UserManageDto userManageDto;

    @Schema(description ="처리 결과: WAIT(처리 전), SUSPENSION(계정 일시 정지), BAN(계정 영구 정지 - 탈퇴와 같은 효과), NP(문제 없음)")
    private ResultType resultType;

    public static ReportedUserDto toDto(User reportedUser) {
        return ReportedUserDto.builder()
                .userManageDto(UserManageDto.toDto(reportedUser))
                .resultType(ResultType.WAIT)
                .build();
    }
}
