package com.dtalks.dtalks.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "email", timeToLive = 300)
@Getter
@AllArgsConstructor
public class EmailAuthentication {

    @Id
    private String code;

    private String email;

    private LocalDateTime createDate;
}
