package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.dto.UserTokenDto;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
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
    public String createAccessToken(UserTokenDto userTokenDto) {
        Claims claims = Jwts.claims().setSubject(userTokenDto.getEmail());
        claims.put("userid", userTokenDto.getUserid());
        claims.put("nickname", userTokenDto.getNickname());
        claims.put("provider", userTokenDto.getProvider());
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
    public String createRefreshToken(UserTokenDto userTokenDto) {
        Claims claims = Jwts.claims().setSubject(userTokenDto.getEmail());
        claims.put("userid", userTokenDto.getUserid());
        claims.put("nickname", userTokenDto.getNickname());
        claims.put("provider", userTokenDto.getProvider());
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
        User user = userRepository.findByEmail(this.getEmailByToken(token)).orElseThrow(
                () -> new CustomException(ErrorCode.VALIDATION_ERROR, "존재하지 않는 사용자입니다."));
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            Date date = Date.from(user.getModifiedDate().atZone(ZoneId.systemDefault()).toInstant());
            // 토큰 발행일이 유저 데이터 수정일 이전이면 유효하지 않은 토큰임
            if(claims.getBody().getIssuedAt().before(date)) {
                throw new CustomException(ErrorCode.VALIDATION_ERROR, "유효하지 않은 토큰입니다.");
            }
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "유효하지 않은 토큰입니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Authentication getAuthentication(String token) {
        User user = userRepository.findByEmail(this.getEmailByToken(token)).orElseThrow(
                () -> new CustomException(ErrorCode.VALIDATION_ERROR, "존재하지 않는 사용자입니다."));
        if (user.getStatus() != ActiveStatus.ACTIVE) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "현재 정지 상태로 활동이 불가능합니다.");
        }

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    @Override
    public String getEmailByToken(String token) {
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        return info;
    }

    @Override
    public User getUserByToken(String token) {
        String email = getEmailByToken(token);
        return userRepository.findByEmail(email).get();
    }
}
