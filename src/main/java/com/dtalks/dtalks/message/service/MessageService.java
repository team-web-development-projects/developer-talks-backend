package com.dtalks.dtalks.message.service;

import com.dtalks.dtalks.message.dto.MessageDto;

import java.util.List;

public interface MessageService {

    List<MessageDto> searchByNickname(String nickname);

    List<MessageDto> searchSentMessage();

    List<MessageDto> searchReceiveMessage();
    Long sendMessage(MessageDto messageDto);

    void deleteMessageBySender(Long id);
    void deleteMessageByReceiver(Long id);
}
