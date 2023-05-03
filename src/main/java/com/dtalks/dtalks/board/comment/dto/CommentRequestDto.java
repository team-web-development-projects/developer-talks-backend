package com.dtalks.dtalks.board.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequestDto {
    @NotBlank
    String content;

    boolean isSecret;
}
