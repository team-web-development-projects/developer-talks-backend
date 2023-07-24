package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.studyroom.dto.ChatMessageDto;
import com.dtalks.dtalks.studyroom.entity.ChatMessage;
import com.dtalks.dtalks.studyroom.entity.ChatRoom;
import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.studyroom.repository.ChatMessageRepository;
import com.dtalks.dtalks.studyroom.repository.ChatRoomRepository;
import com.dtalks.dtalks.studyroom.repository.StudyRoomRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final StudyRoomRepository studyRoomRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatRoom createRoom(Long studyRoomId) {
        ChatRoom chatRoom = new ChatRoom();
        Optional<StudyRoom> optionalStudyRoom = studyRoomRepository.findById(studyRoomId);
        if(optionalStudyRoom.isEmpty()) {
            throw new CustomException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "스터디룸을 찾을수 없습니다.");
        }

        chatRoom.setStudyRoom(optionalStudyRoom.get());
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    @Override
    @Transactional
    public ChatMessageDto createChatMessage(Long chatRoomId, String message, User user) {
        ChatRoom chatRoom = checkChatRoom(chatRoomId);
        checkStudyRoomMember(chatRoom.getStudyRoom(), user);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(user);
        chatMessage.setMessage(message);
        log.info("메세지 저장: " + message);
        chatMessageRepository.save(chatMessage);

        return ChatMessageDto.toDto(chatMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> findAllChat(Long chatRoomId, Pageable pageable) {
        User user = SecurityUtil.getUser();
        ChatRoom chatRoom = checkChatRoom(chatRoomId);
        checkStudyRoomMember(chatRoom.getStudyRoom(), user);
        Page<ChatMessage> chatMessages = chatMessageRepository.findByChatRoom(chatRoom, pageable);
        return chatMessages.map(ChatMessageDto::toDto);
    }

    private void checkStudyRoomMember(StudyRoom studyRoom, User user) {
        for(StudyRoomUser studyRoomUser: studyRoom.getStudyRoomUsers()) {
            if(studyRoomUser.getUser().getEmail().equals(user.getEmail())) {
                return;
            }
        }
        throw new CustomException(ErrorCode.VALIDATION_ERROR, "스터디룸 가입 유저가 아닙니다.");
    }

    private ChatRoom checkChatRoom(Long chatRoomId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if(optionalChatRoom.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "채팅방이 존재하지 않습니다.");
        }
        return optionalChatRoom.get();
    }
}
