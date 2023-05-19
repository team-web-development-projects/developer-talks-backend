package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;

public class FileFormatException extends RuntimeException{

    private ErrorCode errorCode;

    public FileFormatException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
