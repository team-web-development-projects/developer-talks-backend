package com.dtalks.dtalks.notification.controller;

import com.dtalks.dtalks.notification.dto.NotificationDto;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.service.NotificationService;
import com.dtalks.dtalks.notification.service.SseEmitters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    @Operation(description = "알람 구독")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return sseEmitters.subscribe(userDetails.getUsername(), lastEventId);
    }

    @Operation(summary = "모든 알람 WAIT -> READ로 상태 변경")
    @PostMapping(value = "/read/all")
    public void updateReadStatus() {
        notificationService.updateAllStatus();
    }

    @Operation(summary = "특정 알람 WAIT -> READ로 상태 변경")
    @PostMapping(value = "/read/{id}")
    public void updateAllStatus(@PathVariable Long id) {
        notificationService.updateStatus(id);
    }

    @Operation(summary = "사용자에게 온 알람 리스트 전체 보기")
    @GetMapping(value = "/all")
    public ResponseEntity<List<NotificationDto>> findAllByUserid() {
        return ResponseEntity.ok(notificationService.findAllNotificationByUserid());
    }

    @Operation(summary = "알람 리스트에서 읽음 상태 설정", description = "사용자에게 온 알람 status에 해당하는 것만 보기 (READ / WAIT)"
            , parameters = {
            @Parameter(name = "status", description = "알람의 상태 (읽음 READ / 안 읽음 WAIT)")
    })
    @GetMapping(value = "/all/{status}")
    public ResponseEntity<List<NotificationDto>> findAllByStatus(@PathVariable ReadStatus status) {
        return ResponseEntity.ok(notificationService.findAllNotificationByUseridAndStatus(status));
    }

    @Operation(summary = "안 읽은 알람 수")
    @GetMapping(value = "/count")
    public ResponseEntity<Long> countUnreadNotification() {
        return ResponseEntity.ok(notificationService.countUnreadNotification());
    }

    @Operation(summary = "알람 삭제, db에서 삭제")
    @DeleteMapping(value = "/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteById(id);
    }

}
