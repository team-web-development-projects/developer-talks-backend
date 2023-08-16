package com.dtalks.dtalks.board.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "게시글에 대한 사용자의 즐겨찾기, 추천 여부 DTO")
public class FavoriteAndRecommendStatusDto {
    @Schema(description = "즐겨찾기 true / false")
    boolean favorite;

    @Schema(description = "추천 true / false")
    boolean recommend;

    @Builder
    public FavoriteAndRecommendStatusDto(boolean favorite, boolean recommend) {
        this.favorite = favorite;
        this.recommend = recommend;
    }
}
