package com.dtalks.dtalks.user.config;

import com.dtalks.dtalks.user.dto.UserTokenDto;
import com.dtalks.dtalks.user.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(OAuthSuccessHandler.class);
    public final TokenService tokenService;

    @Value("${spring.registration.redirect}")
    private String url;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LOGGER.info("onAuthenticationSuccess 호출됨");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setEmail(oAuth2User.getAttribute("email"));
        userTokenDto.setNickname(oAuth2User.getAttribute("nickname"));
        userTokenDto.setUserid(oAuth2User.getAttribute("userid"));
        userTokenDto.setProvider(oAuth2User.getAttribute("registrationId"));
        String accessToken = tokenService.createAccessToken(userTokenDto);
        String refreshToken = tokenService.createRefreshToken(userTokenDto);

        String targetUrl = UriComponentsBuilder.fromUriString(url)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
