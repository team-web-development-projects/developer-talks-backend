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

    @Operation(description = "로그인")
    @PostMapping(value = "/sign-in")
    public SignInResponseDto signIn(@RequestBody SignInDto signInDto) throws RuntimeException {
        LOGGER.info("POST /sign-in");
        SignInResponseDto signInResponseDto = userService.signIn(signInDto);

        return signInResponseDto;
    }

    @Operation(description = "회원가입")
    @PostMapping(value = "/sign-up")
    public ResponseEntity signUp(@Valid @RequestBody SignUpDto signUpDto) {
        LOGGER.info("POST /sign-up");
        userService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "refresh 토큰을 이용한 토큰 재발급")
    @PostMapping(value = "/token/refresh")
    public ResponseEntity<AccessTokenDto> tokenRefresh(@RequestBody()RefreshTokenDto refreshToken) {
        return ResponseEntity.ok(userService.reSignIn(refreshToken.getRefreshToken()));
    }

    @Operation(summary = "oauth 추가정보 입력")
    @PutMapping(value = "oauth/sign-up")
    public ResponseEntity<SignInResponseDto> oAuthSignUp(@Valid @RequestBody OAuthSignUpDto oAuthSignUpDto) {
        return ResponseEntity.ok(userService.oAuthSignUp(oAuthSignUpDto));
    }

    @Operation(description = "관리자 로그인")
    @PostMapping("/admin/sign-in")
    public ResponseEntity<SignInResponseDto> adminSignIn (@RequestBody SignInDto signInDto) {
        return ResponseEntity.ok(userService.adminSignIn(signInDto));
    }
}
