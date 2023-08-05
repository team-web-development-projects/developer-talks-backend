package com.dtalks.dtalks.admin.report.dto;

import com.dtalks.dtalks.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportedUserInfoDto {
    private Long id;
    private String userid;
    private String email;
    private String nickname;

    public static ReportedUserInfoDto toDto(User user) {
        return ReportedUserInfoDto.builder()
                .id(user.getId())
                .userid(user.getUserid())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
