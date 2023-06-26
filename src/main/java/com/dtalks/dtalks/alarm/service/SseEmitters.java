package com.dtalks.dtalks.alarm.service;

import com.dtalks.dtalks.alarm.dto.AlarmDto;
import com.dtalks.dtalks.alarm.entity.Alarm;
import com.dtalks.dtalks.alarm.enums.AlarmStatus;
import com.dtalks.dtalks.alarm.enums.AlarmType;
import com.dtalks.dtalks.alarm.repository.AlarmRepository;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEmitters {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();
    private final AlarmRepository alarmRepository;

    private Long TIMEOUT = 1000L * 60L * 20L;

    public SseEmitter subscribe(String userid, String lastEventId) {
        String emitterId = makeTimeIncludeId(userid);
        SseEmitter emitter = save(emitterId, new SseEmitter(TIMEOUT));
        emitter.onCompletion(() -> this.emitters.remove(emitterId));
        emitter.onTimeout(() -> this.emitters.remove(emitterId));
        emitter.onError(throwable -> {
            log.error("[SSE] - subscribe error");
            log.error("", throwable);
            emitter.complete();
        });

        // 503 에러 방지하기 위해 더미 이벤트 전송
        String eventId = makeTimeIncludeId(userid);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userid=" + userid + "]");

        // 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방 (Last-Event-ID는 프론트에서 보내주는 것)
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userid, emitterId, emitter);
        }

        return emitter;
    }

    private String makeTimeIncludeId(String userid) {
        return userid + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitters.remove(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userid, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = findAllEventCacheStartWithByUserid(userid);
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public void send(User receiver, AlarmType alarmType, String url) {
        Alarm alarm = alarmRepository.save(createAlarm(receiver, alarmType, url));

        String receiverid = receiver.getUserid();
        String eventId = receiverid + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> emitters = findAllEmitterStartWithByUserid(receiverid);
        emitters.forEach(
                (key, emitter) -> {
                    saveEventCache(key, alarm);
                    sendNotification(emitter, eventId, key, AlarmDto.toDto(alarm));
                }
        );
    }

    private Alarm createAlarm(User receiver, AlarmType alarmType, String url) {
        return Alarm.builder()
                .receiver(receiver)
                .type(alarmType)
                .url(url)
                .alarmStatus(AlarmStatus.WAIT)
                .build();
    }

    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    public Map<String, SseEmitter> findAllEmitterStartWithByUserid(String userid) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userid))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findAllEventCacheStartWithByUserid(String userid) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userid))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteAllEmitterStartWithId(String userid) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(userid)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    public void deleteAllEventCacheStartWithId(String userid) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(userid)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
