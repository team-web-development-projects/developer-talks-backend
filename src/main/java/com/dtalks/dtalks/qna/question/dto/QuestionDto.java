package com.dtalks.dtalks.qna.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionDto {

    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message = "본문을 입력하세요.")
    private String  content;

    @Schema(description = "게시글 첨부파일(이미지), 없어도 됨", example = "[첨부파일, 첨부파일]", nullable = true)
    private List<MultipartFile> files;

}
