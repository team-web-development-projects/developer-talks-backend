package com.dtalks.dtalks.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMTokenManager {
    @Value("${firebase.firebaseConfigPath}")
    private String firebaseConfigPath;

    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(String userId, String token) {
        redisTemplate.opsForValue().set(userId, token);
    }

    public String getToken(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    public void deleteToken(String userId) {
        redisTemplate.delete(userId);
    }

    @Async
    public void deleteAndSaveFCMToken(String userId, String token) {
        if (getToken(userId) != null) {
            redisTemplate.delete(userId);
        }
        redisTemplate.opsForValue().set(userId, token);
    }
}
