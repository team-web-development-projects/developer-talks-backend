package com.dtalks.dtalks.user.dto;

import com.dtalks.dtalks.user.enums.ActivityType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자의 최근활동 dto")
public class RecentActivityDto {

    @Schema(description = "게시글, 댓글 타입에는 게시글의 id, 질문, 답변 관련에는 질문글의 id")
    private Long id;

    @Schema(description = "활동 타입")
    private ActivityType type;

    @Schema(description = "사용자가 상호작용한 글의 제목")
    private String title;
    
    @Schema(description = "사용자가 상호작용한 글의 작성자")
    private String writer;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activityTime;

    @Builder
    public static RecentActivityDto toDto(Long id, ActivityType type, String title, String writer, LocalDateTime activityTime) {
        return RecentActivityDto.builder()
                .id(id)
                .type(type)
                .title(title)
                .writer(writer)
                .activityTime(activityTime)
                .build();
    }
}
