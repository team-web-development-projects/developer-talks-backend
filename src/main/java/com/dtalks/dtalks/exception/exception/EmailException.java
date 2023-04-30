package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;

public class EmailException extends RuntimeException{
    private ErrorCode errorCode;

    public EmailException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
