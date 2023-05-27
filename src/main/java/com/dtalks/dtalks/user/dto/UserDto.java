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
    private boolean isActive;
}
