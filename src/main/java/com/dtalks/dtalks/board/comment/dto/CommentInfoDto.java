package com.dtalks.dtalks.board.comment.dto;

import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.user.dto.UserSimpleDto;
import com.dtalks.dtalks.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "댓글 응답 DTO")
public class CommentInfoDto {
    @Schema(description = "댓글 db에 저장된 id, primary key")
    private Long id;

    @Schema(description = "댓글이 달린 게시글의 id")
    private Long postId;

    @NotBlank
    String content;

    @Schema(description = "작성한 사용자의 닉네임, 이미지")
    UserSimpleDto userInfo;

    @Schema(description = "비밀글 여부. true일 경우 사용자와 게시글 주인만 볼 수 있도록 처리하면 됨")
    boolean secret;

    @Schema(description = "댓글 삭제 여부. 사용자가 댓글을 삭제했는데 자식 댓글이 달려있는 경우로 true일 때 '삭제된 댓글입니다.'로 content에 들어가있음.")
    boolean remove;

    @Schema(description = "부모댓글의 id. 자식 댓글일 경우 부모 댓글 필요.")
    Long parentId;

    @Schema(description = "부모댓글 작성자의 닉네임")
    String parentNickname;

    @Schema(description = "자식 댓글 리스트. 자신이 부모 댓글일 경우 자식 댓글 리스트 필요")
    List<CommentInfoDto> childrenList = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public static CommentInfoDto toDto(Comment comment) {
        User user = comment.getUser();
        String profile = (user.getProfileImage() != null ? user.getProfileImage().getUrl() : null);

        return CommentInfoDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.isRemoved() ? "삭제된 댓글입니다." : comment.getContent())
                .userInfo(UserSimpleDto.createUserInfo(user.getNickname(), profile))
                .secret(comment.isSecret())
                .remove(comment.isRemoved())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .parentNickname(comment.getParent() != null ? comment.getParent().getUser().getNickname() : null)
                .childrenList(new ArrayList<>())
                .createDate(comment.getCreateDate())
                .modifiedDate(comment.getModifiedDate())
                .build();
    }
}
