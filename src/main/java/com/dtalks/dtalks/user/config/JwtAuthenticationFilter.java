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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
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
