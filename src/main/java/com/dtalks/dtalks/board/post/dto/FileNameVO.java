package com.dtalks.dtalks.board.post.dto;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class FileNameVO {
    private final String inputName;
    private final String storeName;
    private final Path savePath;

    public FileNameVO(String inputName, String storeName, Path savePath) {
        this.inputName = inputName;
        this.storeName = storeName;
        this.savePath = savePath;
    }
}
