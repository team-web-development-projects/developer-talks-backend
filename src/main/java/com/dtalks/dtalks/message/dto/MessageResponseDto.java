package com.dtalks.dtalks.message.dto;

import com.dtalks.dtalks.message.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MessageResponseDto {
    private String text;
    private String nickname;

    public static MessageResponseDto toDto(Message message) {
        return MessageResponseDto.builder()
                .text(message.getText())
                .nickname(message.getUser().getNickname())
                .build();
    }
}
