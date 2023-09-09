package com.dtalks.dtalks.user.config;

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

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(OAuthSuccessHandler.class);
    public final TokenService tokenService;

    @Value("${spring.registration.redirect}")
    private String url;
    @Value("${spring.registration.first.redirect}")
    private String firstUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LOGGER.info("onAuthenticationSuccess 호출됨");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Long id = oAuth2User.getAttribute("id");

        boolean isActive = oAuth2User.getAttribute("isActive");

        String targetUrl;
        if(!isActive) {
            targetUrl = UriComponentsBuilder.fromUriString(firstUrl)
                    .queryParam("accessToken", tokenService.createAccessToken(id))
                    .build().toString();
        }
        else {
            String accessToken = tokenService.createAccessToken(id);
            String refreshToken = tokenService.createRefreshToken(id);

            targetUrl = UriComponentsBuilder.fromUriString(url)
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
