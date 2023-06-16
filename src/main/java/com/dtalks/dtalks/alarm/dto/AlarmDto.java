package com.dtalks.dtalks.alarm.dto;

import com.dtalks.dtalks.alarm.enums.AlarmStatus;
import com.dtalks.dtalks.alarm.enums.AlarmType;
import com.dtalks.dtalks.alarm.entity.Alarm;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmDto {
    private Long id;

    @Schema(description = "알림 조회하는 사용자의 닉네임")
    private String receiverNickname;

    @Schema(description = "연결할 url")
    private String url;

    @Schema(description = "댓글 or 추천")
    private AlarmType type;

    @Schema(description = "읽음 or 안읽음 상태 (READ / WAIT)")
    private AlarmStatus alarmStatus;

    @Schema(description = "알람 생성일 -> 누군가 댓글/추천을 한 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @Schema(description = "사용자가 알람을 읽은 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public static AlarmDto toDto(Alarm alarm) {
        return AlarmDto.builder()
                .id(alarm.getId())
                .receiverNickname(alarm.getReceiver().getNickname())
                .url(alarm.getUrl())
                .type(alarm.getType())
                .alarmStatus(alarm.getAlarmStatus())
                .createDate(alarm.getCreateDate())
                .modifiedDate(alarm.getModifiedDate())
                .build();
    }

}
