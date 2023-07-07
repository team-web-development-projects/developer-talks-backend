package com.dtalks.dtalks.board.post.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class NewImageDto {
    private MultipartFile file;
    private Long orderNum;
}
