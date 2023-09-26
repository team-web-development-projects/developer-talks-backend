package com.dtalks.dtalks.user.config;

import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    private static final String[] SHOULD_NOT_FILTER_URI_ALL_LIST = new String[]{
            "/sign-in/**", "/sign-up", "exception", "/users/check/**", "/email/**"
            ,"/token/refresh", "/ws/chat/**", "/sub/**", "/pub/**", "/notifications/**", "/admin/sign-in",
            "**exception**", "/users/recent/activity/**", "/users/private/**"
    };

    private static final String[] SHOULD_NOT_FILTER_URI_GET_LIST = new String[]{
            "/post/**", "/comment/**", "/questions/**", "/answers/**", "/news", "/users/userid",
            "/announcements/**", "/announcements/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (Arrays.stream(SHOULD_NOT_FILTER_URI_ALL_LIST)
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()))) {
            return true;
        } else {
            if (request.getMethod().equals("GET")) {
                return Arrays.stream(SHOULD_NOT_FILTER_URI_GET_LIST)
                        .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("doFilterInternal 호출됨 " + request.getRequestURI());
//        Enumeration<String> headers = request.getHeaderNames();
//        LOGGER.info("헤더: ");
//        while(headers.hasMoreElements()) {
//            String name = (String) headers.nextElement();
//            String value = request.getHeader(name);
//            LOGGER.info(name + ": " + value);
//        }
        String token = tokenService.resolveToken(request);

        try {
            if(token != null && tokenService.validateToken(token)) {
                Authentication authentication = tokenService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (CustomException c) {
            response.setStatus(c.getErrorCode().getStatus());
            response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(c.getMessage());
        }
    }
}
