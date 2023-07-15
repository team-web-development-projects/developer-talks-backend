package com.dtalks.dtalks.studyroom.controller;

import com.dtalks.dtalks.studyroom.dto.ChatMessageDto;
import com.dtalks.dtalks.studyroom.dto.ChatMessageRequestDto;
import com.dtalks.dtalks.studyroom.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/rooms/{chatRoomId}")
    public void message(@DestinationVariable Long chatRoomId, ChatMessageRequestDto chatMessageRequestDto) {
        log.info("채팅 저장: " + chatMessageRequestDto.getMessage());
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .id(chatRoomId)
                .message(chatMessageRequestDto.getMessage())
                .sender("you")
                .createDate(LocalDateTime.now())
                .build();
        simpMessagingTemplate.convertAndSend("/sub/rooms/" + chatRoomId, chatMessageDto);
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
