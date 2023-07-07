package com.dtalks.dtalks.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInResponseDto {

    private String accessToken;
    private String refreshToken;
}
