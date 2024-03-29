package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    @Value("${springboot.jwt.secret}")
    private String secretKey;

    private final long accessTokenValidMillisecond = 1000L * 60 * 60 * 3;
    private final long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24;

    @PostConstruct
    protected void init() {
        // secretKey 를 base64 형식으로 인코딩
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    @Override
    public String createAccessToken(Long id) {
        Claims claims = Jwts.claims().setSubject(Long.toString(id));
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return accessToken;
    }

    @Override
    public String createRefreshToken(Long id) {
        Claims claims = Jwts.claims().setSubject(Long.toString(id));
        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return refreshToken;
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "유효하지 않은 토큰입니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Authentication getAuthentication(String token) {
        User user = userRepository.findById(this.getIdByToken(token)).orElseThrow(
                () -> new CustomException(ErrorCode.VALIDATION_ERROR, "존재하지 않는 사용자입니다."));

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    @Override
    public Long getIdByToken(String token) {
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        return Long.parseLong(info);
    }

    @Override
    public User getUserByToken(String token) {
        return userRepository.findById(getIdByToken(token)).get();
    }
}
