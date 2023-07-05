package com.dtalks.dtalks.notification.dto;

import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationRequestDto {
    Long refId;
    User receiver;
    NotificationType type;
    String message;
    String url;

    public static NotificationRequestDto toDto(Long refId, Long urlId, User receiver, NotificationType type, String message) {
        String url = null;
        switch (type) {
            case COMMENT, RECOMMENT, RECOMMEND_POST: url = "/post/" + urlId; break;
            case RECOMMEND_QUESTION, ANSWER, ANSWER_SELECTED: url = "/questions/" + urlId; break;
            case STUDY_JOIN_REQUEST, STUDY_MEMBER_QUIT, STUDY_LEVEL_UPDATE, STUDY_REQUEST_ACCEPTED: url = "/study-rooms/" + urlId; break;
            case MESSAGE: url = "/messages/received/" + urlId; break;
        }
        return NotificationRequestDto.builder()
                .refId(refId)
                .receiver(receiver)
                .type(type)
                .message(message)
                .url(url)
                .build();
    }
}
