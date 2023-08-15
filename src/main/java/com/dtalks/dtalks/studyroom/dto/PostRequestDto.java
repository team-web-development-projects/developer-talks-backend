package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.enums.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRequestDto {

    @NotNull
    private Long studyRoomId;
    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private Category category;
}
