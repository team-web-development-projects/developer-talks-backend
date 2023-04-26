package com.dtalks.dtalks.user.dto;


import com.dtalks.dtalks.user.entity.User;
import lombok.*;

import java.util.Collections;

@Getter
@Setter
@ToString
public class UserDto {
    private String email;
    private String userid;
    private String nickname;
    private String registrationId;

    public User toUser() {
        return User.builder()
                .email(email)
                .userid(userid)
                .nickname(nickname)
                .registrationId(registrationId)
                .roles(Collections.singletonList("USER"))
                .build();
    }
}
