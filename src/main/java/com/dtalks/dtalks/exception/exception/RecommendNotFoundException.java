package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;

public class RecommendNotFoundException extends RuntimeException{

    private ErrorCode errorCode;

    public RecommendNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
