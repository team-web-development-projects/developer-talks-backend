package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;
import lombok.Getter;

@Getter
public class UserDuplicateException extends RuntimeException{

    private ErrorCode errorCode;

    public UserDuplicateException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
