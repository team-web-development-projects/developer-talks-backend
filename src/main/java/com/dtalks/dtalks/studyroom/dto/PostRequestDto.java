package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRequestDto {

    @NotNull
    @Schema(description = "제목")
    private String title;

    @NotNull
    @Schema(description = "내용")
    private String content;

    @NotNull
    @Schema(description = "카테고리")
    private Category category;
}
