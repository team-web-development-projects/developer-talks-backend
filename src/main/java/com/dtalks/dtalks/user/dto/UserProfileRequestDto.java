package com.dtalks.dtalks.user.dto;

import com.dtalks.dtalks.studyroom.enums.Skill;
import lombok.Getter;

import java.util.List;

@Getter
public class UserProfileRequestDto {

    String description;
    List<Skill> skills;
}
