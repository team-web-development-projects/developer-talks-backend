package com.dtalks.dtalks.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "refresh", timeToLive = 60 * 60 * 24)
@AllArgsConstructor
@Getter
public class RefreshToken {
    @Id
    private String refreshToken;
    private Long id;
    private LocalDateTime createDate;
}
