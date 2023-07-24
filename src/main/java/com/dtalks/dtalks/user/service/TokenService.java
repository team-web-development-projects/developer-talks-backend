package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.dto.UserTokenDto;
import com.dtalks.dtalks.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

public interface TokenService {
    String createAccessToken(UserTokenDto userTokenDto);

    String createRefreshToken(UserTokenDto userTokenDto);

    String resolveToken(HttpServletRequest request);

    String getEmailByToken(String token);

    boolean validateToken(String token);

    Authentication getAuthentication(String token);

    User getUserByToken(String token);
}
