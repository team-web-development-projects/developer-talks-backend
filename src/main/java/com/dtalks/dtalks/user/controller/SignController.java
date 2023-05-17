package com.dtalks.dtalks.user.controller;

import com.dtalks.dtalks.user.dto.*;
import com.dtalks.dtalks.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증, 인가")
@RestController
public class SignController {

    private final Logger LOGGER = LoggerFactory.getLogger(SignController.class);
    private final UserService userService;

    @Autowired
    public SignController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/sign-in")
    public SignInResponseDto signIn(@RequestBody SignInDto signInDto) throws RuntimeException {
        LOGGER.info("POST /sign-in");
        SignInResponseDto signInResponseDto = userService.signIn(signInDto);

        return signInResponseDto;
    }

    @PostMapping(value = "/sign-up")
    public SignUpResponseDto signUp(@Valid @RequestBody SignUpDto signUpDto) {
        LOGGER.info("POST /sign-up");
        SignUpResponseDto signUpResponseDto = userService.signUp(signUpDto);
        return signUpResponseDto;
    }

    @Operation(summary = "refresh 토큰을 이용한 토큰 재발급")
    @PostMapping(value = "token/refresh")
    public ResponseEntity<SignInResponseDto> tokenRefresh(@RequestBody()RefreshTokenDto refreshToken) {
        SignInResponseDto signInResponseDto = userService.reSignIn(refreshToken.getRefreshToken());

        return ResponseEntity.ok(signInResponseDto);
    }
}
