package com.dtalks.dtalks.base.service;

import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.repository.NotificationRepository;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Schedule {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    @Async
    public void deleteNotificationSchedule() {
        log.info("[SCHEDULE] - deleteNotificationSchedule");
        notificationRepository.deleteByCreateDateLessThanAndReadStatus(LocalDateTime.now().minusMonths(1), ReadStatus.READ);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Async
    public void unSuspendAccount() {
        log.info("[SCHEDULE] - unSuspendAccount");
        List<User> suspendedUser = userRepository.findByStatusAndModifiedDateLessThanEqual(ActiveStatus.SUSPENSION, LocalDateTime.now().minusDays(7));
        for (User user : suspendedUser) {
            user.setStatus(ActiveStatus.ACTIVE);
        }
    }

}
