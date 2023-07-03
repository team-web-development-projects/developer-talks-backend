package com.dtalks.dtalks.message.service;

import com.dtalks.dtalks.message.dto.MessageDto;
import com.dtalks.dtalks.message.dto.MessageResponseDto;

public interface MessageService {

    MessageResponseDto searchById(Long id);
    Long createMessage (MessageDto messageDto);

    void deleteMessage(Long id);
}
