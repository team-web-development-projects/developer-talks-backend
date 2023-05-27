package com.dtalks.dtalks.user.dto;

import com.dtalks.dtalks.studyroom.enums.Skill;
import lombok.Data;

import java.util.List;

@Data
public class OAuthSignUpDto {

    private String nickname;
    private List<Skill> skills;
    private String description;
    private Long profileImageId;
}
