package com.dtalks.dtalks.user.config;

import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(OAuthSuccessHandler.class);
    public final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LOGGER.info("onAuthenticationSuccess 호출됨");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String token = jwtTokenProvider.createToken(oAuth2User.getAttribute("email"), Collections.singletonList("USER"));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("accessToken", token);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        PrintWriter printWriter = response.getWriter();
        printWriter.write(jsonObject.toString());
    }
}
