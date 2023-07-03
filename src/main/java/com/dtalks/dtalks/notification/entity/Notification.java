package com.dtalks.dtalks.notification.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.enums.NotificationType;
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
public class Notification extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long refId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadStatus readStatus;

    private String url;

    @Builder
    public static Notification createNotification(Long refId, User receiver, NotificationType type, String message, String url) {
        return Notification.builder()
                .refId(refId)
                .receiver(receiver)
                .type(type)
                .message(message)
                .readStatus(ReadStatus.WAIT)
                .url(url)
                .build();
    }

    public void readDataDeleteSetting() {
        this.refId = null;
        this.url = null;
    }
}
