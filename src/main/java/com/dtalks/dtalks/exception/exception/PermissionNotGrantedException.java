package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;
import lombok.Getter;

@Getter
public class PermissionNotGrantedException extends RuntimeException {
    private ErrorCode errorCode;

    public PermissionNotGrantedException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
