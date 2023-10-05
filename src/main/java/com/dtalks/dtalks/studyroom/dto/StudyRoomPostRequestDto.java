package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyRoomPostRequestDto {

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
