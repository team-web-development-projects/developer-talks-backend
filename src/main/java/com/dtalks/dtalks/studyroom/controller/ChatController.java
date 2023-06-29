package com.dtalks.dtalks.studyroom.controller;

import com.dtalks.dtalks.studyroom.dto.ChatMessageDto;
import com.dtalks.dtalks.studyroom.dto.ChatMessageRequestDto;
import com.dtalks.dtalks.studyroom.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/{chatRoomId}")
    @SendTo("/rooms/{chatRoomId}")
    public ChatMessageDto message(@DestinationVariable Long chatRoomId, ChatMessageRequestDto chatMessageRequestDto) {
        return chatService.createChatMessage(chatRoomId, chatMessageRequestDto.getMessage());
    }

    @Operation(summary = "채팅 가져오기")
    @GetMapping("/{chatRoomId}/chats")
    public ResponseEntity<Page<ChatMessageDto>> searchAll(
            @PageableDefault(size = 100, sort = "createDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable,
            @PathVariable Long chatRoomId
            ) {
        return ResponseEntity.ok(chatService.findAllChat(chatRoomId, pageable));
    }
}
