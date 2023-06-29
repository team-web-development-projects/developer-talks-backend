package com.dtalks.dtalks.studyroom.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private StudyRoom studyRoom;
}
