package com.dtalks.dtalks.admin.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InquiryDto {
    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message = "본문을 입력하세요.")
    private String  content;

    @NotBlank(message = "공개 여부를 선택하세요.")
    private Boolean isPrivate;

}
