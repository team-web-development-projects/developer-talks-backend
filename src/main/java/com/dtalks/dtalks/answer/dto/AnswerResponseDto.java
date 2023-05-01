package com.dtalks.dtalks.answer.dto;

import com.dtalks.dtalks.answer.entity.Answer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public static AnswerResponseDto toDto(Answer answer) {
        return AnswerResponseDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .nickname(answer.getUser().getNickname())
                .build();
    }
}
