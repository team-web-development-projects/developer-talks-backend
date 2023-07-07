package com.dtalks.dtalks.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class DuplicateResponseDto {
    private boolean duplicated;
}
