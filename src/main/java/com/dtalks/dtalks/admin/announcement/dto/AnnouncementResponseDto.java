package com.dtalks.dtalks.admin.announcement.dto;

import com.dtalks.dtalks.admin.announcement.entity.Announcement;
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
@Schema(description = "공지사항 응답 DTO")
public class AnnouncementResponseDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String writer;

    @Schema(description = "공지사항 조회수")
    private Integer viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public static AnnouncementResponseDto toDto(Announcement announcement) {
        return AnnouncementResponseDto.builder()
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .writer("관리자")
                .viewCount(announcement.getViewCount())
                .createDate(announcement.getCreateDate())
                .modifiedDate(announcement.getModifiedDate())
                .build();
    }
}
