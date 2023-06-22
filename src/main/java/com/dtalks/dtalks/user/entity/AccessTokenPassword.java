package com.dtalks.dtalks.user.entity;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "accessToken", timeToLive = 300)
@AllArgsConstructor
public class AccessTokenPassword {

    @Id
    private String accessToken;

    private LocalDateTime createDate;
}
