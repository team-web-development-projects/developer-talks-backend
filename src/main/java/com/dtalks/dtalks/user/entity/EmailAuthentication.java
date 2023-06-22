package com.dtalks.dtalks.user.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
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
