package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.studyroom.enums.Skill;
import com.dtalks.dtalks.user.dto.UserSimpleDto;
import com.dtalks.dtalks.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.hibernate.Hibernate;

import java.util.List;

@Data
@Builder
public class StudyRoomJoinResponseDto {

    @Schema(description = "스터디룸 제목")
    private String title;

    @Schema(description = "스터디룸 id")
    private Long studyRoomId;

    @Schema(description = "스터디룸 유저 id")
    private Long studyRoomUserId;

    @Schema(description = "유저 정보")
    private UserSimpleDto userInfo;

    @Schema(description = "신청자 내 소개")
    private String description;

    @Schema(description = "신청자 스킬")
    private List<Skill> skills;

    public static StudyRoomJoinResponseDto toDto(StudyRoom studyRoom, StudyRoomUser studyRoomUser, User user) {
        Hibernate.initialize(user.getSkills());

        String profile = (user.getProfileImage() != null ? user.getProfileImage().getUrl() : null);
        return StudyRoomJoinResponseDto.builder()
                .studyRoomId(studyRoom.getId())
                .title(studyRoom.getTitle())
                .studyRoomUserId(studyRoomUser.getId())
                .userInfo(UserSimpleDto.createUserInfo(user.getNickname(), profile))
                .description(user.getDescription())
                .skills(user.getSkills())
                .build();
    }
}
