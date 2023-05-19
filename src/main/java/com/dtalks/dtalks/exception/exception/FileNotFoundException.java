package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;

public class FileNotFoundException extends RuntimeException{

    private ErrorCode errorCode;

    public FileNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
