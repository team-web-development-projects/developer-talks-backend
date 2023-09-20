package com.dtalks.dtalks.fcm;

import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FCMListener {
    private final FCMService fcmService;

    @Async
    @TransactionalEventListener
    public void handleNotification(NotificationRequestDto dto) {
        fcmService.sendMessage(dto.getReceiver().getId(), dto.getRefId(), dto.getReceiver(), dto.getType(), dto.getMessage(), dto.getUrl());
    }
}
