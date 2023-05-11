package com.dtalks.dtalks.studyroom.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.studyroom.enums.StudyRoomLevel;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRoomUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StudyRoomLevel studyRoomLevel;

    // 가입 신청중 0, 가입 완료 1
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudyRoom studyRoom;

    @Builder
    public static StudyRoomUser toEntity(User user, StudyRoom studyRoom
            , StudyRoomLevel studyRoomLevel, boolean status) {
        return StudyRoomUser.builder()
                .user(user)
                .studyRoom(studyRoom)
                .studyRoomLevel(studyRoomLevel)
                .status(status)
                .build();
    }
}
