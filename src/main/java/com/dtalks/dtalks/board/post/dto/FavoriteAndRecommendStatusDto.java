package com.dtalks.dtalks.board.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteAndRecommendStatusDto {
    boolean favorite;
    boolean recommend;
}
