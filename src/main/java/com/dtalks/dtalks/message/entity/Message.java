package com.dtalks.dtalks.message.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.message.dto.MessageDto;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Message toEntity(MessageDto messageDto, User user) {
        return Message.builder()
                .text(messageDto.getText())
                .user(user)
                .build();
    }
}
