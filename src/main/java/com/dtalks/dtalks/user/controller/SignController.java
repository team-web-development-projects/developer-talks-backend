package com.dtalks.dtalks.user.controller;

import com.dtalks.dtalks.user.dto.SignInDto;
import com.dtalks.dtalks.user.dto.SignInResponseDto;
import com.dtalks.dtalks.user.dto.SignUpDto;
import com.dtalks.dtalks.user.dto.SignUpResponseDto;
import com.dtalks.dtalks.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
