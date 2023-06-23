package com.dtalks.dtalks.board.post.dto;

import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.user.dto.UserSimpleDto;
import com.dtalks.dtalks.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @Schema(description = "게시글 썸네일")
    private String thumbnailUrl;

    @Schema(description = "이미지 urls")
    private List<String> imageUrls;

    @Schema(description = "작성한 사용자의 닉네임, 프로필 이미지")
    UserSimpleDto userInfo;

    @Schema(description = "게시글의 댓글수")
    private Integer commentCount;

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
        User user = post.getUser();
        String profile = (user.getProfileImage() != null ? user.getProfileImage().getUrl() : null);

        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .thumbnailUrl(post.getThumbnailUrl())
                .userInfo(UserSimpleDto.createUserInfo(user.getNickname(), profile))
                .commentCount(post.getCommentCount())
                .viewCount(post.getViewCount())
                .favoriteCount(post.getFavoriteCount())
                .recommendCount(post.getRecommendCount())
                .createDate(post.getCreateDate())
                .modifiedDate(post.getModifiedDate())
                .build();
    }
}
