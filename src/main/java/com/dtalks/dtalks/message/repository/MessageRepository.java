package com.dtalks.dtalks.message.repository;

import com.dtalks.dtalks.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
