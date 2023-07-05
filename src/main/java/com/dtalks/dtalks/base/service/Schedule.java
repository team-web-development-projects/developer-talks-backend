package com.dtalks.dtalks.base.service;

import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class Schedule {
    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    @Async
    public void deleteSchedule() {
        notificationRepository.deleteByCreateDateLessThanAndReadStatus(LocalDateTime.now().minusMonths(1), ReadStatus.READ);
    }
}
