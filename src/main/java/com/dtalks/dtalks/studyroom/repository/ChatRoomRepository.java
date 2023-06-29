package com.dtalks.dtalks.studyroom.repository;

import com.dtalks.dtalks.studyroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
