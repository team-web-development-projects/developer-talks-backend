package com.dtalks.dtalks.board.comment.dto;

import com.dtalks.dtalks.board.comment.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentInfoDto {
    private Long id;

    private Long postId;

    @NotBlank
    String content;

    @NotBlank
    String nickname;

    boolean isSecret;

    boolean isRemoved;

    Long parentId;

    List<CommentInfoDto> childrenList = new ArrayList<>();

    @Builder
    public static CommentInfoDto toDto(Comment comment) {
        return CommentInfoDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.isRemoved() ? "삭제된 댓글입니다." : comment.getContent())
                .nickname(comment.getUser().getNickname())
                .isSecret(comment.isSecret())
                .isRemoved(comment.isRemoved())
                .childrenList(new ArrayList<>())
                .build();
    }
}
