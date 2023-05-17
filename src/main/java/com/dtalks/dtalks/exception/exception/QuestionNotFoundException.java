package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;

public class QuestionNotFoundException extends RuntimeException{

    private ErrorCode errorCode;

    public QuestionNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}