package com.dtalks.dtalks.studyroom.repository;

import com.dtalks.dtalks.studyroom.entity.ChatMessage;
import com.dtalks.dtalks.studyroom.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatRoom(ChatRoom chatRoom, Pageable pageable);
}
