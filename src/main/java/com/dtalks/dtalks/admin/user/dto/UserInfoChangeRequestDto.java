package com.dtalks.dtalks.admin.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoChangeRequestDto {
    @NotNull
    private String nickname;
    @NotNull
    private String email;
}
