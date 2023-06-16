package com.dtalks.dtalks.alarm.repository;

import com.dtalks.dtalks.alarm.enums.AlarmStatus;
import com.dtalks.dtalks.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByReceiverId(Long userId);
    List<Alarm> findByReceiverIdAndAlarmStatus(Long userId, AlarmStatus status);
    Long countByReceiverIdAndAlarmStatus(Long userId, AlarmStatus status);
}
