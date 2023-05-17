package com.dtalks.dtalks.exception.exception;

import com.dtalks.dtalks.exception.ErrorCode;
import lombok.Getter;

@Getter
public class SelectedAnswerNotFoundException extends RuntimeException {
    private ErrorCode errorCode;

    public SelectedAnswerNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
