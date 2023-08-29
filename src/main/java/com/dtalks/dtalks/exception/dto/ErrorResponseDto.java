package com.dtalks.dtalks.exception.dto;

import com.dtalks.dtalks.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "에러 응답 DTO")
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
