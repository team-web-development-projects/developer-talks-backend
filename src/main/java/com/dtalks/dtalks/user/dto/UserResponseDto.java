package com.dtalks.dtalks.user.dto;

import com.dtalks.dtalks.studyroom.enums.Skill;
import com.dtalks.dtalks.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponseDto {
    private String userid;
    private String nickname;
    private String email;
    private List<Skill> skills;
    private String description;
    private String registrationId;

    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .userid(user.getUserid())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .skills(user.getSkills())
                .description(user.getDescription())
                .registrationId(user.getRegistrationId())
                .build();
    }
}
