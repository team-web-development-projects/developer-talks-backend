package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.entity.StudyRoomPost;
import com.dtalks.dtalks.studyroom.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudyRoomPostDto {

    @Schema(description = "pk")
    private Long id;

    @NotNull
    @Schema(description = "제목")
    private String title;

    @NotNull
    @Schema(description = "내용")
    private String content;

    @Schema(description = "카테고리")
    private Category category;

    @Schema(description = "조회수")
    private Integer viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public static StudyRoomPostDto toDto(StudyRoomPost studyRoomPost) {
        return StudyRoomPostDto.builder()
                .id(studyRoomPost.getId())
                .title(studyRoomPost.getTitle())
                .content(studyRoomPost.getContent())
                .category(studyRoomPost.getCategory())
                .viewCount(studyRoomPost.getViewCount())
                .createDate(studyRoomPost.getCreateDate())
                .modifiedDate(studyRoomPost.getModifiedDate())
                .build();
    }
}
