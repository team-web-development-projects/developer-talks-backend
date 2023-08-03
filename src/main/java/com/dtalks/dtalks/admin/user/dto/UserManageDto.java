package com.dtalks.dtalks.admin.user.dto;

import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserManageDto {
    private Long id;
    private String userid;
    private String email;
    private String nickname;
    private ActiveStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public static UserManageDto toDto(User user) {
        return UserManageDto.builder()
                .id(user.getId())
                .userid(user.getUserid())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .createDate(user.getCreateDate())
                .modifiedDate(user.getModifiedDate())
                .build();
    }
}
