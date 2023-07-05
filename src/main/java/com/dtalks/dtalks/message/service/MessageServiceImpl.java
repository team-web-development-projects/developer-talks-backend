package com.dtalks.dtalks.message.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.message.dto.MessageDto;
import com.dtalks.dtalks.message.entity.Message;
import com.dtalks.dtalks.message.repository.MessageRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public MessageDto searchById(Long id) {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND_ERROR, "쪽지가 존재하지 않습니다. ");
        }
        Message message = messageOptional.get();
        MessageDto messageDto = MessageDto.toDto(message);
        return messageDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> searchSentMessage() {
        User user = SecurityUtil.getUser();
        List<Message> messageList = messageRepository.findBySender(user);

        if (messageList.isEmpty()) {
            throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND_ERROR, "보낸 쪽지가 존재하지 않습니다. ");
        }

        List<MessageDto>  sentMessages = messageList.stream()
                .filter(message -> !message.isDeletedBySender())
                .map(MessageDto::toDto)
                .collect(Collectors.toList());

        return sentMessages;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> searchReceiveMessage() {
        User user = SecurityUtil.getUser();
        List<Message> messageList = messageRepository.findByReceiver(user);

        if (messageList.isEmpty()) {
            throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND_ERROR, "받은 쪽지가 존재하지 않습니다. ");
        }

        List<MessageDto>  receivedMessages = messageList.stream()
                .filter(message -> !message.isDeletedByReceiver())
                .map(MessageDto::toDto)
                .collect(Collectors.toList());

        return receivedMessages;
    }

    @Override
    @Transactional
    public Long sendMessage(MessageDto messageDto) {
        User sender = SecurityUtil.getUser();

        Optional<User> optionalReceiver = userRepository.findByNickname(messageDto.getReceiverNickname());
        if (optionalReceiver.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 회원입니다. ");
        }
        User receiver = optionalReceiver.get();

        Message message = Message.toEntity(messageDto, sender, receiver);
        messageRepository.save(message);
        return message.getId();
    }

    @Override
    @Transactional
    public void deleteMessageBySender(Long id) {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty() || messageOptional.get().isDeletedBySender()) {
            throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND_ERROR, "쪽지가 존재하지 않습니다. ");
        }
        Message message = messageOptional.get();

        User user = SecurityUtil.getUser();

        if (!user.getUserid().equals(message.getSender().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "삭제할 수 있는 권한이 없습니다");
        }

        message.setDeletedBySender();

        if (message.isDeleted()) {
            messageRepository.delete(message);
        }
    }

    @Override
    @Transactional
    public void deleteMessageByReceiver(Long id) {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isEmpty()) {
            throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND_ERROR, "쪽지가 존재하지 않습니다. ");
        }
        Message message = messageOptional.get();

        User user = SecurityUtil.getUser();

        if (!user.getUserid().equals(message.getReceiver().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "삭제할 수 있는 권한이 없습니다");
        }

        message.setDeletedByReceiver();

        if (message.isDeleted()) {
            messageRepository.delete(message);
        }
    }
}