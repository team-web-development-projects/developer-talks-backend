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

    @Operation(description = "특정 알람 하나 조회. WAIT -> READ로 상태 변경. 엔드포인트 String 반환 (ex. /post/1", parameters = {
        @Parameter(name = "id", description = "조회할 알람의 id")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<String> findByIdAndUpdateStatus(@PathVariable Long id) {
        return ResponseEntity.ok(alarmService.findByIdAndUpdateStatus(id));
    }

    @Operation(description = "사용자에게 온 알람 전체 보기")
    @GetMapping(value = "/all")
    public ResponseEntity<List<AlarmDto>> findAllByUserid() {
        return ResponseEntity.ok(alarmService.findAllAlarmByUserid());
    }

    @Operation(description = "사용자에게 온 알람 status에 해당하는 것만 보기 (READ / WAIT)", parameters = {
            @Parameter(name = "status", description = "알람의 상태 (읽음 READ / 안 읽음 WAIT)")
    })
    @GetMapping(value = "/all/{status}")
    public ResponseEntity<List<AlarmDto>> findAllByStatus(@PathVariable AlarmStatus status) {
        return ResponseEntity.ok(alarmService.findAllAlarmByUseridAndStatus(status));
    }

    @GetMapping(value = "/count")
    public ResponseEntity<Long> countUnreadAlarm() {
        return ResponseEntity.ok(alarmService.countUnreadAlarm());
    }

    @DeleteMapping(value = "/{id}")
    public void deleteAlarm(@PathVariable Long id) {
        alarmService.deleteById(id);
    }

}
