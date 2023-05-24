package com.dtalks.dtalks.user.controller;

import com.dtalks.dtalks.base.dto.DocumentResponseDto;
import com.dtalks.dtalks.user.dto.DuplicateResponseDto;
import com.dtalks.dtalks.user.dto.UserResponseDto;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.service.UserDetailsService;
import com.dtalks.dtalks.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Tag(name = "users")
@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    @Autowired
    public UserController(UserDetailsService userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Operation(summary = "userid 중복 체크")
    @GetMapping(value = "/check/userid/{userid}")
    public ResponseEntity<DuplicateResponseDto> useridDuplicateCheck(@PathVariable String userid) {
        DuplicateResponseDto duplicateResponseDto = userService.useridDuplicated(userid);
        return ResponseEntity.ok(duplicateResponseDto);
    }

    @Operation(summary = "nickname 중복 체크")
    @GetMapping(value = "/check/nickname/{nickname}")
    public ResponseEntity<DuplicateResponseDto> nicknameDuplicatedCheck(@PathVariable String nickname) {
        DuplicateResponseDto duplicateResponseDto = userService.nicknameDuplicated(nickname);
        return  ResponseEntity.ok(duplicateResponseDto);
    }

    @Operation(summary = "유저 정보")
    @GetMapping(value = "/info")
    public ResponseEntity<UserResponseDto> userInformation(@AuthenticationPrincipal User user) {
        LOGGER.info("user info controller");
        UserResponseDto userResponseDto = userService.userInfo();
        return ResponseEntity.ok(userResponseDto);
    }

    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping(value = "/profile/image"
    , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentResponseDto> profileImageUpLoad(@RequestPart("file") MultipartFile file) {
        LOGGER.info("profileImageUpLoad controller 호출됨");
        DocumentResponseDto documentResponseDto = userService.userProfileImageUpLoad(file);

        return ResponseEntity.ok(documentResponseDto);
    }

    @Operation(summary = "유저 소개글 수정")
    @PutMapping(value = "/profile/description")
    public ResponseEntity<UserResponseDto> updateUserDescription(@RequestBody String description) {
        LOGGER.info("updateUserDescription controller 호출됨");
        return ResponseEntity.ok(userService.updateUserDescription(description));
    }
}
