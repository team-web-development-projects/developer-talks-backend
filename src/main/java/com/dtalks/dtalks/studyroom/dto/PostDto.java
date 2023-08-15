package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.entity.Post;
import com.dtalks.dtalks.studyroom.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {

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

    public static PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .viewCount(post.getViewCount())
                .createDate(post.getCreateDate())
                .modifiedDate(post.getModifiedDate())
                .build();
    }
}
