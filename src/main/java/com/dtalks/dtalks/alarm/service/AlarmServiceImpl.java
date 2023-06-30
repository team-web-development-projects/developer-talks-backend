package com.dtalks.dtalks.alarm.service;

import com.dtalks.dtalks.alarm.dto.AlarmDto;
import com.dtalks.dtalks.alarm.entity.Alarm;
import com.dtalks.dtalks.alarm.enums.AlarmStatus;
import com.dtalks.dtalks.alarm.repository.AlarmRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;

    @Override
    @Transactional
    public void updateStatus(Long id) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(id);
        if (optionalAlarm.isEmpty()) {
            throw new CustomException(ErrorCode.ALARM_NOT_FOUND_ERROR, "해당하는 알람을 찾을 수 없습니다.");
        }

        Alarm alarm = optionalAlarm.get();
        alarm.setAlarmStatus(AlarmStatus.READ);
    }

    @Override
    @Transactional
    public void updateAllStatus() {
        User user = SecurityUtil.getUser();
        List<Alarm> alarmList = alarmRepository.findByReceiverIdAndAlarmStatus(user.getId(), AlarmStatus.WAIT);
        for (Alarm alarm : alarmList) {
            alarm.setAlarmStatus(AlarmStatus.READ);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlarmDto> findAllAlarmByUserid() {
        User user = SecurityUtil.getUser();
        List<Alarm> alarmList = alarmRepository.findByReceiverId(user.getId());
        return alarmList.stream().map(AlarmDto::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlarmDto> findAllAlarmByUseridAndStatus(AlarmStatus status) {
        User user = SecurityUtil.getUser();
        List<Alarm> alarmList = alarmRepository.findByReceiverIdAndAlarmStatus(user.getId(), status);
        return alarmList.stream().map(AlarmDto::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnreadAlarm() {
        User user = SecurityUtil.getUser();
        return alarmRepository.countByReceiverIdAndAlarmStatus(user.getId(), AlarmStatus.WAIT);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = SecurityUtil.getUser();
        alarmRepository.deleteById(id);
    }

}
