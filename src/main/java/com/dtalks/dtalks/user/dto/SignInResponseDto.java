package com.dtalks.dtalks.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class SignInResponseDto extends SignUpResponseDto {

    private String accessToken;
    private String refreshToken;

    @Builder
    public SignInResponseDto(boolean success, int code, String msg, String accessToken, String refreshToken) {
        super(success, code, msg);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
