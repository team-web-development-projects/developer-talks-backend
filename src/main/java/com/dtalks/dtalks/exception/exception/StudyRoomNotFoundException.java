package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;

public class StudyRoomNotFoundException extends RuntimeException{

    private ErrorCode errorCode;

    public StudyRoomNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
