package com.dtalks.dtalks.base.dto;


import com.dtalks.dtalks.base.entity.Document;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentResponseDto {

    private long id;

    private String url;

    private String inputName;

    public static DocumentResponseDto toDto(Document document) {
        return DocumentResponseDto.builder()
                .url(document.getUrl())
                .inputName(document.getInputName())
                .id(document.getId())
                .build();
    }
}
