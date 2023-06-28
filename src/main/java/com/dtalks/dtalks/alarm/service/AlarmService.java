package com.dtalks.dtalks.alarm.service;

import com.dtalks.dtalks.alarm.dto.AlarmDto;
import com.dtalks.dtalks.alarm.enums.AlarmStatus;

import java.util.List;

public interface AlarmService {
    void updateStatus(Long id);
    void updateAllStatus();
    List<AlarmDto> findAllAlarmByUserid();
    List<AlarmDto> findAllAlarmByUseridAndStatus(AlarmStatus status);
    Long countUnreadAlarm();
    void deleteById(Long id);
}
