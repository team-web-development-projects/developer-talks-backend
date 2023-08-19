package com.dtalks.dtalks.notification.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    public Notification (Long refId, User receiver, NotificationType type, String message, String url) {
        this.refId = refId;
        this.receiver = receiver;
        this.type = type;
        this.message = message;
        this.readStatus = ReadStatus.WAIT;
        this.url = url;
    }

    public void updateReadStatus() {
        this.readStatus = ReadStatus.READ;
    }

    public void readDataDeleteSetting() {
        this.refId = null;
        this.url = null;
    }
}
