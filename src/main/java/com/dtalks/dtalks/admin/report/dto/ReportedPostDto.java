package com.dtalks.dtalks.admin.report.dto;

import com.dtalks.dtalks.board.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "신고 당한 게시글 리스트 조회시에 보는 dto")
public class ReportedPostDto {

    @Schema(description = "신고한 게시글의 id")
    private Long postId;

    @Schema(description = "신고한 게시글의 제목")
    private String title;

    @Schema(description = "신고한 게시글 작성자 id")
    private Long writerId;

    @Schema(description = "신고한 게시글 작성자의 닉네임")
    private String writerNickname;

    @Schema(description ="신고한 게시글 작성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    private boolean processed;

    public static ReportedPostDto toDto(Post post) {
        return ReportedPostDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .writerId(post.getUser().getId())
                .writerNickname(post.getUser().getNickname())
                .createDate(post.getCreateDate())
                .processed(post.isForbidden())
                .build();
    }

}
