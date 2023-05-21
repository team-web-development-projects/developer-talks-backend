package com.dtalks.dtalks.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpDto{

    @Schema(description = "이메일")
    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @Schema(description = "닉네임")
    @Size(min = 1, max = 30, message = "닉네임은 1~30자 사이이어야 합니다.")
    private String nickname;

    @Schema(description = "아이디")
    @Size(min = 5, max = 15, message = "id는 5~15자 사이이어야 합니다.")
    private String userid;

    @Schema(description = "비밀번호")
    @Pattern(regexp = "(?=.*\\W)(?=\\S+$).{8,15}",
            message = "특수기호가 적어도 1개 이상 포함된 5~15자의 비밀번호이어야 합니다.")
    private String password;

    @Schema(description = "프로필 이미지 id")
    private long profileImageId;
}
