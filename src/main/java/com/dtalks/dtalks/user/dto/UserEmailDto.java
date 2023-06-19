package com.dtalks.dtalks.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class UserEmailDto {

    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;
}
