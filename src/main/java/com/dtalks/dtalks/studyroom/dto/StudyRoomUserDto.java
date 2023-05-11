package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.studyroom.enums.StudyRoomLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyRoomUserDto {
    private StudyRoomLevel studyRoomLevel;
    private boolean status;

    private String nickname;

    @Builder
    public static StudyRoomUserDto toDto(StudyRoomUser studyRoomUser) {
        return StudyRoomUserDto.builder()
                .studyRoomLevel(studyRoomUser.getStudyRoomLevel())
                .status(studyRoomUser.isStatus())
                .nickname(studyRoomUser.getUser().getNickname())
                .build();
    }
}
