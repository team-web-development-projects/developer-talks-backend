package com.dtalks.dtalks.studyroom.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.studyroom.dto.StudyRoomRequestDto;
import com.dtalks.dtalks.studyroom.enums.Skill;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int joinableCount;

    @Column(nullable = false)
    private int joinCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @Column(nullable = false)
    private boolean autoJoin;

    @OneToMany(mappedBy = "studyRoom", cascade = CascadeType.ALL)
    private List<StudyRoomUser> studyRoomUsers = new ArrayList<>();

    @Builder
    public static StudyRoom toEntity(StudyRoomRequestDto studyRoomRequestDto) {
        return StudyRoom.builder()
                .title(studyRoomRequestDto.getTitle())
                .content(studyRoomRequestDto.getContent())
                .joinableCount(studyRoomRequestDto.getJoinableCount())
                .skills(studyRoomRequestDto.getSkills())
                .joinCount(1)
                .autoJoin(studyRoomRequestDto.isAutoJoin())
                .build();
    }

    public void addJoinCount() {
        joinCount += 1;
    }

    public void addStudyRoomUser(StudyRoomUser studyRoomUser) {
        studyRoomUsers.add(studyRoomUser);
    }
}
