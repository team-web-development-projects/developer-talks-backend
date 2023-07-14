package com.dtalks.dtalks.notification.repository;

import com.dtalks.dtalks.notification.entity.Notification;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverId(Long userId);
    List<Notification> findByReceiverIdOrderByCreateDateDesc(Long userId);
    List<Notification> findByReceiverIdAndReadStatus(Long userId, ReadStatus status);
    Long countByReceiverIdAndReadStatus(Long userId, ReadStatus status);

    Optional<Notification> findByRefIdAndType(Long refId, NotificationType type);
    List<Notification> findByRefIdAndTypeIn(Long refId, List<NotificationType> type);

    void deleteByCreateDateLessThanAndReadStatus(LocalDateTime deleteDate, ReadStatus readStatus);
}
