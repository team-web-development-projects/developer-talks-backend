package com.dtalks.dtalks.studyroom.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private StudyRoom studyRoom;

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();
}
