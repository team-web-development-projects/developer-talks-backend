package com.dtalks.dtalks.notification.service;

import com.dtalks.dtalks.notification.dto.NotificationDto;
import com.dtalks.dtalks.notification.enums.ReadStatus;

import java.util.List;

public interface NotificationService {
    void updateStatus(Long id);
    void updateAllStatus();
    List<NotificationDto> findAllNotificationByUserid();
    List<NotificationDto> findAllNotificationByUseridAndStatus(ReadStatus status);
    Long countUnreadNotification();
    void deleteById(Long id);
}
