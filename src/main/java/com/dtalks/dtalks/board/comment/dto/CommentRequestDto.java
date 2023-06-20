package com.dtalks.dtalks.board.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "댓글 생성, 수정에 쓰이는 요청 DTO")
public class CommentRequestDto {
    @NotBlank
    String content;

    @Schema(description = "비밀글 여부.")
    boolean secret;
}
