package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface TokenService {
    String createAccessToken(Long id);

    String createRefreshToken(Long id);

    String resolveToken(HttpServletRequest request);

    String getEmailByToken(String token);

    boolean validateToken(String token);

    Authentication getAuthentication(String token);

    User getUserByToken(String token);
}
