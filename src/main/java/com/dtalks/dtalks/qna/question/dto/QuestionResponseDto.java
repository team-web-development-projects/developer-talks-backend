package com.dtalks.dtalks.qna.question.dto;

import com.dtalks.dtalks.qna.question.entity.Question;
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
@Schema(description = "질문글 응답 DTO")
public class QuestionResponseDto {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Schema(description = "이미지 urls")
    private List<String> imageUrls;

    @Schema(description = "작성한 사용자의 닉네임, 이미지")
    UserSimpleDto userInfo;

    @Schema(description = "질문글 조회수")
    private Integer viewCount;

    @Schema(description = "게시글 즐겨찾기수")
    private Integer favoriteCount;

    @Schema(description = "질문글 추천수")
    private Integer recommendCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public static QuestionResponseDto toDto(Question question) {
        User user = question.getUser();
        String profile = (user.getProfileImage() != null ? user.getProfileImage().getUrl() : null);

        return QuestionResponseDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .userInfo(UserSimpleDto.createUserInfo(user.getNickname(), profile))
                .viewCount(question.getViewCount())
                .favoriteCount(question.getFavoriteCount())
                .recommendCount(question.getRecommendCount())
                .createDate(question.getCreateDate())
                .modifiedDate(question.getModifiedDate())
                .build();
    }
}
