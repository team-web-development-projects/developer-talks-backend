package com.dtalks.dtalks.studyroom.dto;

import com.dtalks.dtalks.studyroom.enums.Skill;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StudyRoomRequestDto {

    @NotBlank
    private String title;
    private String content;
    private List<Skill> skills;

    @NotNull
    private boolean autoJoin;

    @Min(value = 1)
    private int joinableCount;
}
