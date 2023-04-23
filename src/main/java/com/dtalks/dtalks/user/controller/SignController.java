package com.dtalks.dtalks.user.controller;

import com.dtalks.dtalks.user.dto.SignInDto;
import com.dtalks.dtalks.user.dto.SignInResponseDto;
import com.dtalks.dtalks.user.dto.SignUpDto;
import com.dtalks.dtalks.user.dto.SignUpResponseDto;
import com.dtalks.dtalks.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignController {

    private final UserService userService;

    @Autowired
    public SignController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/sign-in")
    public SignInResponseDto signIn(@RequestBody SignInDto signInDto) throws RuntimeException {
        SignInResponseDto signInResponseDto = userService.signIn(signInDto);

        return signInResponseDto;
    }

    @PostMapping(value = "/sign-up")
    public SignUpResponseDto signUp(@Valid @RequestBody SignUpDto signUpDto) {
        SignUpResponseDto signUpResponseDto = userService.signUp(signUpDto);
        return signUpResponseDto;
    }
}
