package com.dtalks.dtalks.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DuplicateResponseDto {
    private boolean duplicated;
}
