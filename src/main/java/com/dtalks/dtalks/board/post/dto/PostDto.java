package com.dtalks.dtalks.board.post.dto;

import com.dtalks.dtalks.board.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "게시글 응답 DTO")
public class PostDto {

    @Schema(description = "db에 저장되어 있는 게시글의 primary key")
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Schema(description = "게시글을 작성한 사용자의 닉네임")
    @NotBlank
    private String nickname;

    @Schema(description = "게시글 조회수")
    private Integer viewCount;

    @Schema(description = "게시글 즐겨찾기수")
    private Integer favoriteCount;

    @Schema(description = "게시글 추천수")
    private Integer recommendCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public static PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(post.getUser().getNickname())
                .viewCount(post.getViewCount())
                .favoriteCount(post.getFavoriteCount())
                .recommendCount(post.getRecommendCount())
                .createDate(post.getCreateDate())
                .modifiedDate(post.getModifiedDate())
                .build();
    }
}
