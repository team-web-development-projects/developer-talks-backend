package com.dtalks.dtalks.exception.dto;

import com.dtalks.dtalks.exception.ErrorCode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ErrorResponseDto {

    private int status;

    private String errorType;
    private String message;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.errorType = errorCode.getErrorType();
        this.message = errorCode.getMessage();
    }
    public ErrorResponseDto(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus();
        this.errorType = errorCode.getErrorType();
        this.message = message;
    }

    @Override
    public String toString() {
        return status + " " + errorType + " " + message;
    }
}
