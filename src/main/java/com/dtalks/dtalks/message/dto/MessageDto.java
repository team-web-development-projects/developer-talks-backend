package com.dtalks.dtalks.message.dto;

import com.dtalks.dtalks.message.entity.Message;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MessageDto {
    private Long id;

    @NotBlank(message = "보낸이 닉네임이 필요합니다. ")
    private String senderNickname;
    @NotBlank(message = "받는이 닉네임이 필요합니다. ")
    private String receiverNickname;
    @NotBlank(message = "쪽지 내용이 필요합니다. ")
    private String text;

    public static MessageDto toDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .senderNickname(message.getSender().getNickname())
                .receiverNickname(message.getReceiver().getNickname())
                .text(message.getText())
                .build();
    }
}
