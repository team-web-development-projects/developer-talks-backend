package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.dto.UserTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserDetailsService userDetailsService;
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
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByEmail(this.getEmailByToken(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public String getEmailByToken(String token) {
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        return info;
    }
}
