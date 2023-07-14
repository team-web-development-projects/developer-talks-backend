package com.dtalks.dtalks.qna.answer.dto;

import com.dtalks.dtalks.qna.answer.entity.Answer;
import com.dtalks.dtalks.user.dto.UserSimpleDto;
import com.dtalks.dtalks.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponseDto {
    private Long id;

    @NotBlank
    private String content;

    @NotBlank
    private String nickname;

    @Schema(description = "작성한 사용자의 닉네임, 이미지")
    UserSimpleDto userInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public static AnswerResponseDto toDto(Answer answer) {
        User user = answer.getUser();
        String profile = (user.getProfileImage() != null ? user.getProfileImage().getUrl() : null);

        return AnswerResponseDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .nickname(answer.getUser().getNickname())
                .userInfo(UserSimpleDto.createUserInfo(user.getNickname(), profile))
                .createDate(answer.getCreateDate())
                .modifiedDate(answer.getModifiedDate())
                .build();
    }
}
