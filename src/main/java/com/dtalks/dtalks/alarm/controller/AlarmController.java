package com.dtalks.dtalks.alarm.controller;

import com.dtalks.dtalks.alarm.dto.AlarmDto;
import com.dtalks.dtalks.alarm.enums.AlarmStatus;
import com.dtalks.dtalks.alarm.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

//    @Operation(description = "알람 구독")
//    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails,
//                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
//        return sseEmitters.subscribe(userDetails.getUsername(), lastEventId);
//    }

    @Operation(summary = "모든 알람 WAIT -> READ로 상태 변경")
    @PostMapping
    public void updateAllStatus() {
        alarmService.updateAllStatus();
    }

    @Operation(summary = "특정 알람 WAIT -> READ로 상태 변경")
    @PostMapping(value = "/{id}")
    public void updateAllStatus(@PathVariable Long id) {
        alarmService.updateStatus(id);
    }

    @Operation(summary = "사용자에게 온 알람 리스트 전체 보기")
    @GetMapping(value = "/all")
    public ResponseEntity<List<AlarmDto>> findAllByUserid() {
        return ResponseEntity.ok(alarmService.findAllAlarmByUserid());
    }

    @Operation(summary = "알람 리스트에서 읽음 상태 설정", description = "사용자에게 온 알람 status에 해당하는 것만 보기 (READ / WAIT)"
            , parameters = {
            @Parameter(name = "status", description = "알람의 상태 (읽음 READ / 안 읽음 WAIT)")
    })
    @GetMapping(value = "/all/{status}")
    public ResponseEntity<List<AlarmDto>> findAllByStatus(@PathVariable AlarmStatus status) {
        return ResponseEntity.ok(alarmService.findAllAlarmByUseridAndStatus(status));
    }

    @Operation(summary = "안 읽은 알람 수")
    @GetMapping(value = "/count")
    public ResponseEntity<Long> countUnreadAlarm() {
        return ResponseEntity.ok(alarmService.countUnreadAlarm());
    }

    @Operation(summary = "알람 삭제, db에서 삭제")
    @DeleteMapping(value = "/{id}")
    public void deleteAlarm(@PathVariable Long id) {
        alarmService.deleteById(id);
    }

}
