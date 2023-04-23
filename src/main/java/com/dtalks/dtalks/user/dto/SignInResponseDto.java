package com.dtalks.dtalks.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class SignInResponseDto extends SignUpResponseDto {

    private String accessToken;

    @Builder
    public SignInResponseDto(boolean success, int code, String msg, String token) {
        super(success, code, msg);
        this.accessToken = token;
    }
}
