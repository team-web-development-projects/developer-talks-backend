package com.dtalks.dtalks.board.post.dto;

import com.dtalks.dtalks.board.comment.dto.CommentInfoDto;
import com.dtalks.dtalks.board.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String nickname;

    private List<CommentInfoDto> commentList = new ArrayList<>();

    private Integer viewCount;

    private Integer favoriteCount;

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
                .commentList(new ArrayList<>())
                .viewCount(post.getViewCount())
                .favoriteCount(post.getFavoriteCount())
                .createDate(post.getCreateDate())
                .modifiedDate(post.getModifiedDate())
                .build();
    }
}
