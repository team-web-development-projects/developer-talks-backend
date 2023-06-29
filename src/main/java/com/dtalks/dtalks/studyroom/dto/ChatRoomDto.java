package com.dtalks.dtalks.studyroom.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRoomDto {

    private String roomId;
    private String name;

    public static ChatRoomDto create(String name) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setRoomId(UUID.randomUUID().toString());
        chatRoomDto.setName(name);
        return chatRoomDto;
    }
}
