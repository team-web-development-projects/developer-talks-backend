package com.dtalks.dtalks.user.controller;

import com.dtalks.dtalks.base.dto.DocumentResponseDto;
import com.dtalks.dtalks.exception.dto.ErrorResponseDto;
import com.dtalks.dtalks.user.dto.*;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.service.UserActivityService;
import com.dtalks.dtalks.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "users")
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserActivityService userActivityService;


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
        return ResponseEntity.ok(duplicateResponseDto);
    }

    @Operation(summary = "email 중복체크")
    @GetMapping(value = "/check/email/{email}")
    public ResponseEntity<DuplicateResponseDto> emailDuplicatedCheck(@PathVariable String email) {
        return ResponseEntity.ok(userService.emailDuplicated(email));
    }


    @Operation(summary = "유저 정보")
    @GetMapping(value = "/info")
    public ResponseEntity<UserResponseDto> userInformation() {
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
        return ResponseEntity.ok(userService.userProfileImageUpLoad(file));
    }

    @Operation(summary = "프로필 이미지 조회")
    @GetMapping(value = "/profile/image")
    public ResponseEntity<DocumentResponseDto> getUserProfileImage() {
        return ResponseEntity.ok(userService.getUserProfileImage());
    }

    @Operation(summary = "프로필 이미지 수정(기존 이미지 없어도 됨)")
    @PutMapping(value = "/profile/image"
    , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentResponseDto> updateProfileImage(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateUserProfileImage(file));
    }

    @Operation(summary = "유저 소개글, 기술스택 수정")
    @PutMapping(value = "/profile")
    public ResponseEntity<UserResponseDto> updateUserProfile(@RequestBody UserProfileRequestDto userProfileRequestDto) {
        LOGGER.info("updateUserDescription controller 호출됨");
        return ResponseEntity.ok(userService.updateProfile(userProfileRequestDto));
    }

    @GetMapping(value = "/recent/activity/{nickname}")
    @Operation(summary = "특정 유저의 최근활동 조회 (페이지 사용, size = 10, sort=\"createDate\" desc 적용)", parameters = {
            @Parameter(name = "nickname", description = "조회할 유저의 닉네임")
    }, responses = {
            @ApiResponse(responseCode = "202", description = "비공개 설정 사용자를 존재함", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "해당 사용자가 db에 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<Page<RecentActivityDto>> getRecentActivities(@AuthenticationPrincipal UserDetails userDetails,
                                                                       @PathVariable String nickname,
                                                                       @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userActivityService.getRecentActivities(userDetails, nickname, pageable));

    }

    @Operation(summary = "특정 유저의 비공개 여부 조회", parameters = {
            @Parameter(name = "nickname", description = "조회할 유저의 닉네임"),
    }, responses = {
            @ApiResponse(responseCode = "400", description = "해당 사용자가 db에 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping(value = "/private/{nickname}")
    public ResponseEntity<Boolean> getPrivateStatus(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.getPrivateStatus(nickname));
    }

    @Operation(summary = "특정 유저의 비공개 여부 설정", parameters = {
            @Parameter(name = "status", description = "비공개 설정 true/false")
    })
    @PutMapping(value = "/setting/private/{status}")
    public void updatePrivate(@PathVariable boolean status) {
        userService.updatePrivate(status);
    }

    @Operation(summary = "유저 닉네임 변경")
    @PutMapping(value = "/profile/nickname")
    public ResponseEntity<UserResponseDto> updateNickname(@RequestBody UserNicknameDto userNicknameDto) {
        return ResponseEntity.ok(userService.updateNickname(userNicknameDto));
    }

    @Operation(summary = "유저 아이디 변경")
    @PutMapping(value = "/profile/userid")
    public ResponseEntity<UserResponseDto> updateUserid(
            @RequestBody @Valid UseridDto useridDto
    ) {
        return ResponseEntity.ok(userService.updateUserid(useridDto));
    }

    @Operation(summary = "유저 비밀번호 변경")
    @PutMapping(value = "/profile/password")
    public ResponseEntity<Void> updateUserPassword(
            @RequestBody @Valid UserPasswordDto userPasswordDto
    ) {
        userService.updatePassword(userPasswordDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 이메일 변경")
    @PutMapping(value = "/profile/email")
    public ResponseEntity<UserResponseDto> updateUserEmail(
            @RequestBody @Valid UserEmailDto userEmailDto
    ) {
        return ResponseEntity.ok(userService.updateEmail(userEmailDto));
    }

    @Operation(summary = "유저 아이디 찾기")
    @GetMapping(value = "/userid")
    public ResponseEntity findUserid(@RequestParam String email) {
        userService.findUserid(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 비밀번호 찾기(변경)")
    @PutMapping(value = "/password")
    public ResponseEntity findUserPassword(HttpServletRequest request, @RequestBody UserPasswordFindDto userPasswordFindDto) {
        userService.findUserPassword(request, userPasswordFindDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping()
    public ResponseEntity quitUser(@RequestBody UserSimplePasswordDto passwordDto) {
        userService.quitUser(passwordDto);
        return ResponseEntity.ok().build();
    }
}
