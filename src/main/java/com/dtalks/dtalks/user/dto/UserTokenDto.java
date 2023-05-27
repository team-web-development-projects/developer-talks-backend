package com.dtalks.dtalks.user.dto;

import com.dtalks.dtalks.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserTokenDto {
    private String email;
    private String userid;
    private String nickname;

    private String provider;
    private List<String> roles;

    public static UserTokenDto toDto(User user) {
        return UserTokenDto.builder()
                .email(user.getEmail())
                .userid(user.getUserid())
                .nickname(user.getNickname())
                .provider(user.getRegistrationId())
                .build();
    }
}
