package com.dtalks.dtalks.notification.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.notification.dto.NotificationDto;
import com.dtalks.dtalks.notification.entity.Notification;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.repository.NotificationRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void updateStatus(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND_ERROR, "해당하는 알람을 찾을 수 없습니다."));
        notification.updateReadStatus();
    }

    @Override
    @Transactional
    public void updateAllStatus() {
        User user = SecurityUtil.getUser();
        List<Notification> notificationList = notificationRepository.findByReceiverIdAndReadStatus(user.getId(), ReadStatus.WAIT);
        for (Notification notification : notificationList) {
            notification.updateReadStatus();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findAllNotificationByUserid() {
        User user = SecurityUtil.getUser();
        List<Notification> notificationList = notificationRepository.findByReceiverIdOrderByCreateDateDesc(user.getId());
        return notificationList.stream().map(NotificationDto::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findAllNotificationByUseridAndStatus(ReadStatus status) {
        User user = SecurityUtil.getUser();
        List<Notification> notificationList = notificationRepository.findByReceiverIdAndReadStatus(user.getId(), status);
        return notificationList.stream().map(NotificationDto::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnreadNotification() {
        User user = SecurityUtil.getUser();
        return notificationRepository.countByReceiverIdAndReadStatus(user.getId(), ReadStatus.WAIT);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }

}
