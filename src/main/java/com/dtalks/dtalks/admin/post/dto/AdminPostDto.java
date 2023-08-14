package com.dtalks.dtalks.admin.post.dto;

import com.dtalks.dtalks.board.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "관리자 - 작성된 게시글(커뮤니티) 조회")
public class AdminPostDto {
    private Long id;
    private String nickname;
    private String title;

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

    public static AdminPostDto toDto(Post post) {
        return AdminPostDto.builder()
                .id(post.getId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .favoriteCount(post.getFavoriteCount())
                .recommendCount(post.getRecommendCount())
                .createDate(post.getCreateDate())
                .modifiedDate(post.getModifiedDate())
                .build();
    }
}
