package com.dtalks.dtalks.fcm;

import com.dtalks.dtalks.notification.entity.Notification;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.notification.repository.NotificationRepository;
import com.dtalks.dtalks.user.entity.User;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {
    private final NotificationRepository notificationRepository;
    private final FCMTokenManager fcmTokenManager;

    @Transactional
    public void sendMessage(Long userId, Long refId, User receiver, NotificationType type, String content, String url) {
        String token = fcmTokenManager.getToken(String.valueOf(userId));
        notificationRepository.save(Notification.builder().refId(refId).receiver(receiver).type(type).message(content).url(url).build());

        log.info("[FCMService]- sendMessage");
        if (token != null) {
            com.google.firebase.messaging.Notification notification = com.google.firebase.messaging.Notification.builder()
                    .setTitle("Developer-Talks")
                    .setBody(content).build();
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();

            try {
                String messageResponse = FirebaseMessaging.getInstance().sendAsync(message).get();
                log.info("[FCMService] - Sent Message: {}", messageResponse);
            } catch (ExecutionException | InterruptedException e) {
                throw new IllegalStateException("알림 전송에 실패하였습니다.");
            }
        }
    }
}
