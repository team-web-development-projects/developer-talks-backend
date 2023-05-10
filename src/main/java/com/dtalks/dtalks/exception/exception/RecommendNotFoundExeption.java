package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;

public class RecommendNotFoundExeption extends RuntimeException{

    private ErrorCode errorCode;

    public RecommendNotFoundExeption(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
