package com.dtalks.dtalks.message.controller;

import com.dtalks.dtalks.message.dto.MessageDto;
import com.dtalks.dtalks.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "쪽지 리스트 받는이 닉네임으로 조회")
    @GetMapping("/byNickname/{nickname}")
    public ResponseEntity<List<MessageDto>> searchByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(messageService.searchByNickname(nickname));
    }

    @Operation(summary = "보낸 쪽지함 조회")
    @GetMapping("/sent")
    public ResponseEntity<List<MessageDto>> searchSentMessage() {
        return ResponseEntity.ok(messageService.searchSentMessage());
    }
    @Operation(summary = "받은 쪽지함 조회")
    @GetMapping("/received")
    public ResponseEntity<List<MessageDto>> searchReceiveMessage() {
        return ResponseEntity.ok(messageService.searchReceiveMessage());
    }
    @Operation(summary = "쪽지 전송")
    @PostMapping
    public ResponseEntity<Long> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(messageService.sendMessage(messageDto));
    }
    @Operation(summary = "보낸 쪽지 삭제")
    @DeleteMapping("/sent/{id}")
    public void deleteMessageBySender(@PathVariable Long id) {
        messageService.deleteMessageBySender(id);
    }
    @Operation(summary = "받은 쪽지 삭제")
    @DeleteMapping("/received/{id}")
    public void deleteMessageByReceiver(@PathVariable Long id) {
        messageService.deleteMessageByReceiver(id);
    }
}
