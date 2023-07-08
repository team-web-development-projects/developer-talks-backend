package com.dtalks.dtalks.board.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PutRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Schema(description = "기존 게시글 이미지 url. 없었으면 값 없이 title, content만 주면 됨", nullable = true)
    private List<OldImageDto> imgUrls;

    @Schema(description = "새로운 첨부파일(이미지), 없어도 됨.", example = "[첨부파일, 첨부파일]", nullable = true)
    private List<NewImageDto> files;
}
