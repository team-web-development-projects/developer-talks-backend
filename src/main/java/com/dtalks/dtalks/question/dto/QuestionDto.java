package com.dtalks.dtalks.question.dto;

import com.dtalks.dtalks.question.entity.Question;
import com.dtalks.dtalks.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
