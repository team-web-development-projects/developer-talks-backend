package com.dtalks.dtalks.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UseridDto {

    @Size(min = 5, max = 15, message = "아이디의 길이는 5~15 사이여야 합니다.")
    String userid;
}
