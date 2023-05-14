package com.dtalks.dtalks.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserTokenDto {
    private String email;
    private String userid;
    private String nickname;

    private String provider;
    private List<String> roles;
}
