package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatMessageDto {

    @Schema
    private Long id;
    private String sender;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    public static ChatMessageDto toDto(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .id(chatMessage.getId())
                .sender(chatMessage.getSender().getNickname())
                .message(chatMessage.getMessage())
                .build();
    }
}
