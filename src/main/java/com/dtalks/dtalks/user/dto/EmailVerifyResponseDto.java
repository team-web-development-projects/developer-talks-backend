package com.dtalks.dtalks.user.dto;

import lombok.Data;

@Data
public class EmailVerifyResponseDto {
    String email;
    String code;
}
