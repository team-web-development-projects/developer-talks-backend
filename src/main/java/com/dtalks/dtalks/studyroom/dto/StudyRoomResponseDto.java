package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.studyroom.enums.Skill;
import com.dtalks.dtalks.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class StudyRoomResponseDto {
    private Long id;
    private String title;
    private String content;
    private List<Skill> skills;
    private boolean autoJoin;
    private List<StudyRoomUserDto> studyRoomUsers;

    @Builder
    public static StudyRoomResponseDto toDto(StudyRoom studyRoom) {
        List<StudyRoomUserDto> studyRoomUserDtos = new ArrayList<>();
        for(StudyRoomUser studyRoomUser: studyRoom.getStudyRoomUsers()) {
            studyRoomUserDtos.add(StudyRoomUserDto.toDto(studyRoomUser));
        }
        return StudyRoomResponseDto.builder()
                .id(studyRoom.getId())
                .title(studyRoom.getTitle())
                .content(studyRoom.getContent())
                .skills(studyRoom.getSkills())
                .autoJoin(studyRoom.isAutoJoin())
                .studyRoomUsers(studyRoomUserDtos)
                .build();
    }
}
