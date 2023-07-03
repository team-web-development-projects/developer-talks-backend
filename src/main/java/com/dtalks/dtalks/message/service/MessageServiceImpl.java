package com.dtalks.dtalks.message.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.message.dto.MessageDto;
import com.dtalks.dtalks.message.dto.MessageResponseDto;
import com.dtalks.dtalks.message.entity.Message;
import com.dtalks.dtalks.message.repository.MessageRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;

    @Override
    public MessageResponseDto searchById(Long id) {
        Optional<Message> messageOptional = messageRepository.findById(id);
        Message message = messageOptional.get();
        MessageResponseDto messageResponseDto = MessageResponseDto.toDto(message);
        return messageResponseDto;
    }

    @Override
    public Long createMessage(MessageDto messageDto) {
        User user = SecurityUtil.getUser();
        Message message = Message.toEntity(messageDto, user);
        messageRepository.save(message);
        return message.getId();
    }

    @Override
    public void deleteMessage(Long id) {
        Optional<Message> messageOptional = messageRepository.findById(id);
        Message message = messageOptional.get();
        User user = SecurityUtil.getUser();

        if (!user.equals(message.getUser())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "삭제할 수 있는 권한이 없습니다");
        }

        messageRepository.delete(message);
    }
}
