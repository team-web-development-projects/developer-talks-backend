package com.dtalks.dtalks.user.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String userid;
    private String nickname;
    private String email;
    private String registrationId;
}
