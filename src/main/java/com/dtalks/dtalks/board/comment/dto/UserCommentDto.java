package com.dtalks.dtalks.board.comment.dto;

import com.dtalks.dtalks.board.comment.entity.Comment;
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
@Schema(description = "사용자의 댓글 리스트 DTO")
public class UserCommentDto {
    @Schema(description = "댓글 db에 저장된 id, primary key")
    private Long id;

    @Schema(description = "댓글을 단 게시글의 id, 게시글 조회할 때 보내는 것")
    private Long postId;

    @Schema(description = "댓글을 단 게시글의 제목")
    private String postTitle;

    @NotBlank
    String content;

    @Schema(description = "비밀글 여부")
    boolean isSecret;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;
    @Builder
    public static UserCommentDto toDto(Comment comment, Long postId, String title) {
        return UserCommentDto.builder()
                .id(comment.getId())
                .postId(postId)
                .postTitle(title)
                .content(comment.getContent())
                .isSecret(comment.isSecret())
                .createDate(comment.getCreateDate())
                .modifiedDate(comment.getModifiedDate())
                .build();
    }
}
