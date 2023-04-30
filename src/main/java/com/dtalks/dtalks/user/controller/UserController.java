package com.dtalks.dtalks.user.controller;

import com.dtalks.dtalks.user.dto.DuplicateResponseDto;
import com.dtalks.dtalks.user.dto.UserDto;
import com.dtalks.dtalks.user.dto.UserResponseDto;
import com.dtalks.dtalks.user.service.UserDetailsService;
import com.dtalks.dtalks.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Autowired
    public UserController(UserDetailsService userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @GetMapping(value = "check/{userid}")
    public ResponseEntity<DuplicateResponseDto> useridDuplicateCheck(@PathVariable String userid) {
        DuplicateResponseDto duplicateResponseDto = userService.useridDuplicated(userid);
        return ResponseEntity.ok(duplicateResponseDto);
    }

    @PostMapping(value = "check/{nickname}")
    public  ResponseEntity<DuplicateResponseDto> nicknameDuplicatedCheck(@PathVariable String nickname) {
        DuplicateResponseDto duplicateResponseDto = userService.nicknameDuplicated(nickname);
        return  ResponseEntity.ok(duplicateResponseDto);
    }
}
