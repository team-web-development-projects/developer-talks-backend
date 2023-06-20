package com.dtalks.dtalks.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "게시글, 댓글 등에 쓰이는 사용자 관련 정보 dto")
public class UserSimpleDto {

    @NotBlank
    @Schema(description = "작성한 사용자의 닉네임")
    private String nickname;

    @Schema(description = "사용자 이미지")
    private String userProfile;

    @Builder
    public static UserSimpleDto createUserInfo(String nickname, String userProfile) {
        return UserSimpleDto.builder()
                .nickname(nickname)
                .userProfile(userProfile)
                .build();
    }

}
