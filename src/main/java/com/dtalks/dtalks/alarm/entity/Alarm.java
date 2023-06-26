package com.dtalks.dtalks.alarm.entity;

import com.dtalks.dtalks.alarm.enums.AlarmStatus;
import com.dtalks.dtalks.alarm.enums.AlarmType;
import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alarm extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private AlarmType type;

    private String message;

    @Enumerated(EnumType.STRING)
    private AlarmStatus alarmStatus;

    private String url;

    @Builder
    public static Alarm createAlarm(User receiver, AlarmType type, String message, String url) {
        return Alarm.builder()
                .receiver(receiver)
                .type(type)
                .message(message)
                .alarmStatus(AlarmStatus.WAIT)
                .url(url)
                .build();
    }

}
