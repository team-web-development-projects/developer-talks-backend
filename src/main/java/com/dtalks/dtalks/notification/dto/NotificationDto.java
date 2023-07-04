package com.dtalks.dtalks.notification.dto;

import com.dtalks.dtalks.notification.entity.Notification;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private Long id;

    private Long refId;

    @Schema(description = "내용")
    private String message;

    @Schema(description = "연결할 url")
    private String url;

    @Schema(description = "알람 타입 - 댓글/답변/스터디 등")
    private NotificationType type;

    @Schema(description = "읽음 or 안 읽음 상태 (READ / WAIT)")
    private ReadStatus readStatus;

    @Schema(description = "알람 생성일 -> 누군가 댓글/추천을 한 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @Schema(description = "사용자가 알람을 읽은 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public static NotificationDto toDto(Notification noti) {
        return NotificationDto.builder()
                .id(noti.getId())
                .refId(noti.getRefId())
                .message(noti.getMessage())
                .url(noti.getUrl())
                .type(noti.getType())
                .readStatus(noti.getReadStatus())
                .createDate(noti.getCreateDate())
                .modifiedDate(noti.getModifiedDate())
                .build();
    }

}
