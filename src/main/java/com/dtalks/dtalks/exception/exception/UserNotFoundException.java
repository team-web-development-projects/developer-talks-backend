package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private ErrorCode errorCode;

    public UserNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
