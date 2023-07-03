package com.dtalks.dtalks.message.repository;

import com.dtalks.dtalks.message.entity.Message;
import com.dtalks.dtalks.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySender(User user);

    List<Message> findByReceiver(User user);
}
