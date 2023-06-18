package com.dtalks.dtalks.board.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Schema(description = "게시글 첨부파일(이미지), 없어도 됨", example = "[첨부파일, 첨부파일]", nullable = true)
    private List<MultipartFile> files;
}
