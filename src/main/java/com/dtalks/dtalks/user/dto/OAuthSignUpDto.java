package com.dtalks.dtalks.user.dto;

import com.dtalks.dtalks.studyroom.enums.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OAuthSignUpDto {

    private String nickname;
    private List<Skill> skills;
    private String description;
    private long profileImageId;
}
