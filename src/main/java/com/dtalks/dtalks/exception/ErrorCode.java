package com.dtalks.dtalks.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USER_DUPLICATION_ERROR(400, "USER-DUPLICATION", "USER DUPLICATION ERROR"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL-SERVER-ERROR", "INTERNAL SERVER ERROR"),
    AUTHENTICATION_ERROR(401, "AUTHENTICATION-ERROR", "AUTHENTICATION ERROR"),
    VALIDATION_ERROR(400, "VALIDATION-ERROR", "VALIDATION ERROR"),
    PERMISSION_NOT_GRANTED_ERROR(403, "PERMISSION-NOT-GRANTED", "PERMISSION NOT GRANTED ERROR"),
    DELETE_NOT_PERMITTED_ERROR(403, "DELETE-NOT-PERMITTED", "DELETE NOT PERMITTED ERROR"),

    EMAIL_ERROR(400, "EMAIL-ERROR", "EMAIL ERROR"),

    USER_NOT_FOUND_ERROR(400, "USER-NOT_FOUND", "USER NOT FOUND ERROR"),
    POST_NOT_FOUND_ERROR(400, "POST-NOT-FOUND", "POST NOT FOUND ERROR"),
    FAVORITE_POST_NOT_FOUND_ERROR(400, "FAVORITE-POST-NOT-FOUND", "FAVORITE POST NOT FOUND ERROR"),
    RECOMMEND_POST_NOT_FOUND_ERROR(400, "RECOMMEND-POST-NOT-FOUND", "RECOMMEND POST NOT FOUND ERROR: "),
    COMMENT_NOT_FOUND_ERROR(400, "COMMENT-NOT-FOUND", "COMMNET NOT FOUND ERROR"),

    QUESTION_NOT_FOUND_ERROR(400, "QUESTION_NOT_FOUND", "QUESTION_NOT_FOUND_ERROR"),
    ANSWER_NOT_FOUND_ERROR(400, "ANSWER_NOT_FOUND", "ANSWER_NOT_FOUND_ERROR"),

    SELECTED_ANSWER_NOT_FOUND_ERROR(400, "SELECTED_ANSWER_NOT_FOUND", "SELECTED_ANSWER_NOT_FOUND_ERROR"),

    STUDYROOM_NOT_FOUND_ERROR(400, "STUDYROOM-NOT-FOUND", "STUDYROOM NOT FOUND ERROR"),
    RECOMMENDATION_NOT_FOUND_ERROR(400, "RECOMMENDATION-NOT-FOUND-ERROR", "RECOMMENDATION NOT FOUND_ERROR"),

    ALREADY_EXISTS_ERROR(409, "ALREADY_EXISTS-ERROR", "ALREADY EXISTS ERROR"),
    RECOMMENDATION_ALREADY_EXIST_ERROR(400, "RECOMMENDATION-ALREADY-EXIST", "RECOMMENDATION ALREADY EXIST"),
    FILE_NOT_FOUND_ERROR(400, "FILE-NOT-FOUND-ERROR", "FILE NOT FOUND ERROR"),
    FILE_FORMAT_ERROR(400, "FILE-FORMAT-ERROR", "FILE FORMAT ERROR"),

    ACTIVITY_NOT_FOUND_ERROR(400, "ACTIVITY-NOT-FOUND-ERROR", "ACTIVITY NOT FOUND"),
    FILE_UPLOAD_ERROR(500, "FILE-UPLOAD-ERROR", "FILE UPLOAD ERROR"),
    ALARM_NOT_FOUND_ERROR(400, "ALARM-NOT-FOUND-ERROR", "ALARM NOT FOUND");

    private int status;

    private String errorType;

    private String message;
}
