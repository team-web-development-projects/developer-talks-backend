package com.dtalks.dtalks.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserPasswordDto {

    @Schema(description = "비밀번호")
    @Pattern(regexp = "(?=.*\\W)(?=\\S+$).{8,15}",
            message = "특수기호가 적어도 1개 이상 포함된 5~15자의 비밀번호이어야 합니다.")
    @NotBlank
    private String newPassword;

    @Schema(description = "비밀번호 확인")
    private String checkNewPassword;

    @Schema(description = "현재 비밀번호")
    private String oldPassword;
}
