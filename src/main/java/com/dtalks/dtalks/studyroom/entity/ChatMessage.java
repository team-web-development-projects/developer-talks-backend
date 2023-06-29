package com.dtalks.dtalks.studyroom.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User sender;

    private String message;
}
