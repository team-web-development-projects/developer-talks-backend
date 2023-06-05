package com.dtalks.dtalks.base.service;

import com.dtalks.dtalks.user.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class Schedule {

    @Autowired
    private final ActivityRepository activityRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    @Async
    public void activityDeleteSchedule() {
        activityRepository.deleteByCreateDateLessThan(LocalDateTime.now().minusMonths(1));
    }
}
