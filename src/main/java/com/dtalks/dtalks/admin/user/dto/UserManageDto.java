package com.dtalks.dtalks.admin.user.dto;

import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
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
    private Boolean isActive;
    private ActiveStatus status;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    public static UserManageDto toDto(User user) {
        return UserManageDto.builder()
                .id(user.getId())
                .userid(user.getUserid())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .isActive(user.getIsActive())
                .status(user.getStatus())
                .createDate(user.getCreateDate())
                .modifiedDate(user.getModifiedDate())
                .build();
    }
}
