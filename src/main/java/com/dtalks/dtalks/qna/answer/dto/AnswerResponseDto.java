package com.dtalks.dtalks.qna.answer.dto;

import com.dtalks.dtalks.qna.answer.entity.Answer;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public static AnswerResponseDto toDto(Answer answer) {
        return AnswerResponseDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .nickname(answer.getUser().getNickname())
                .createDate(answer.getCreateDate())
                .modifiedDate(answer.getModifiedDate())
                .build();
    }
}
