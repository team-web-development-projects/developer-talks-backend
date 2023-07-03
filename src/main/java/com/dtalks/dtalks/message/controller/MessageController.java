package com.dtalks.dtalks.message.controller;

import com.dtalks.dtalks.message.dto.MessageDto;
import com.dtalks.dtalks.message.dto.MessageResponseDto;
import com.dtalks.dtalks.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.searchById(id));
    }

    @PostMapping
    public ResponseEntity<Long> createMessage(@Valid MessageDto messageDto) {
        return ResponseEntity.ok(messageService.createMessage(messageDto));
    }

    @DeleteMapping
    public void deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
    }
}
