package com.dtalks.dtalks.qna.question.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    @NotBlank
    private String title;

    @NotBlank
    private String  content;

}
