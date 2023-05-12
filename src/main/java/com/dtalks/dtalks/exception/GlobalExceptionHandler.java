package com.dtalks.dtalks.exception;

import com.dtalks.dtalks.exception.dto.ErrorResponseDto;
import com.dtalks.dtalks.exception.exception.*;
import com.dtalks.dtalks.user.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<ErrorResponseDto> handleUserDuplicateException(UserDuplicateException exception) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(exception.getErrorCode(), exception.getMessage());
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(exception.getErrorCode().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception exception) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(BindingResult bindingResult) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.VALIDATION_ERROR, bindingResult.getFieldErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.USER_NOT_FOUND_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePostNotFoundException(PostNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.POST_NOT_FOUND_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(FavoritePostNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePostNotFoundException(FavoritePostNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.FAVORITE_POST_NOT_FOUND_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage() + ex.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(RecommendPostNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRecommendPostNotFoundException(RecommendPostNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.RECOMMEND_POST_NOT_FOUND_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage() + ex.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(PermissionNotGrantedException.class)
    public ResponseEntity<ErrorResponseDto> handlePermissionNotGrantedException(PermissionNotGrantedException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.PERMISSION_NOT_GRANTED_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(DeleteNotPermittedException.class)
    public ResponseEntity<ErrorResponseDto> handleDeleteNotPermittedException(DeleteNotPermittedException exception) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.DELETE_NOT_PERMITTED_ERROR);
        LOGGER.info(("error: " + errorResponseDto.getMessage()));
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorResponseDto> handleMailException(EmailException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.EMAIL_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCommentNotFoundException(CommentNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.COMMENT_NOT_FOUND_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }
    @ExceptionHandler(RecommendationAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleRecommendationAlreadyExistException(RecommendationAlreadyExistException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.RECOMMENDATION_ALREADY_EXIST_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }
    @ExceptionHandler(RecommendNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRecommendNotFoundException(RecommendNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.RECOMMENDATION_NOT_FOUND_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(StudyRoomNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleStudyRoomNotFoundException(StudyRoomNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.STUDYROOM_NOT_FOUND_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyExistsException(AlreadyExistsException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.ALREADY_EXISTS_ERROR);
        LOGGER.info("error : " + errorResponseDto.getMessage() + ex.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }
}
