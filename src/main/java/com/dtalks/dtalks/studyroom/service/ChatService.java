package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.studyroom.dto.ChatMessageDto;
import com.dtalks.dtalks.studyroom.dto.ChatRoomDto;
import com.dtalks.dtalks.studyroom.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {


    public ChatRoom createRoom(Long studyRoomId);

    public ChatMessageDto createChatMessage(Long chatRoomId, String message);

    public Page<ChatMessageDto> findAllChat(Long chatRoomId, Pageable pageable);
}
