package com.dtalks.dtalks.studyroom.controller;

import com.dtalks.dtalks.studyroom.dto.ChatMessageDto;
import com.dtalks.dtalks.studyroom.dto.ChatMessageRequestDto;
import com.dtalks.dtalks.studyroom.service.ChatService;
import com.dtalks.dtalks.user.entity.User;
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
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/rooms/{chatRoomId}")
    public void message(@DestinationVariable Long chatRoomId, ChatMessageRequestDto chatMessageRequestDto, StompHeaderAccessor stompHeaderAccessor) {
        Authentication authentication = (Authentication) stompHeaderAccessor.getUser();
        User user = (User) authentication.getPrincipal();
        log.info("채팅: " + chatMessageRequestDto.getMessage() + " " + authentication.getName() + " " + user.getNickname());
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .id(chatRoomId)
                .message(chatMessageRequestDto.getMessage())
                .sender(user.getNickname())
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
