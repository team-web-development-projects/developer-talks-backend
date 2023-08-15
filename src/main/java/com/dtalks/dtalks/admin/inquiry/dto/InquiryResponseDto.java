package com.dtalks.dtalks.admin.inquiry.dto;


import com.dtalks.dtalks.admin.inquiry.entity.Inquiry;
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
@Schema(description = "문의사항 응답 DTO")
public class InquiryResponseDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String writer;

    @Schema(description = "문의사항 조회수")
    private Integer viewCount;

    @Schema(description = "문의사항 비밀글 여부")
    private Boolean isPrivate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public static InquiryResponseDto toDto(Inquiry inquiry) {
        return InquiryResponseDto.builder()
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .writer(inquiry.getUser().getNickname())
                .viewCount(inquiry.getViewCount())
                .isPrivate(inquiry.getIsPrivate())
                .createDate(inquiry.getCreateDate())
                .modifiedDate(inquiry.getModifiedDate())
                .build();
    }
}
