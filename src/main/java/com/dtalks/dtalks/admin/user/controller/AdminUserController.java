package com.dtalks.dtalks.admin.user.controller;

import com.dtalks.dtalks.admin.user.dto.UserInfoChangeRequestDto;
import com.dtalks.dtalks.admin.user.dto.UserManageDto;
import com.dtalks.dtalks.admin.user.service.UserManageService;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserManageService userManageService;

    @Operation(summary = "사용자 전체 조회 (탈퇴 사용자 제외)", description = "파라미터 없으면 탈퇴 사용자 제외 전체 조회, 있다면 해당하는 타입의 사용자만 조회",
            parameters = {
            @Parameter(name = "pageable", description = "페이지, size=10, sort=id,desc 적용 중"),
    })
    @GetMapping
    public ResponseEntity<Page<UserManageDto>> searchAllUsersExceptQuit(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) ActiveStatus status) {
        return ResponseEntity.ok(userManageService.searchAllUsersExceptQuit(pageable, status));
    }

    @Operation(summary = "회원 정보 수정(닉네임, 이메일)")
    @PutMapping("/update/info")
    public ResponseEntity<UserManageDto> updateUserInfo(@RequestParam Long id, @RequestBody @Valid UserInfoChangeRequestDto dto) {
        return ResponseEntity.ok(userManageService.updateUserInfo(id, dto));
    }

    @Operation(summary = "회원 임시 비밀번호 발급")
    @PutMapping("/update/password")
    public ResponseEntity<Void> updateUserPassword(@RequestParam Long id) {
        userManageService.updateUserPassword(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리자에 의한 사용자 계정 정지, type에는 SUSPENSION, BAN만 들어가야됨")
    @PutMapping("/suspend")
    public ResponseEntity<Void> suspendUser(@RequestParam Long id, @RequestParam ActiveStatus type) {
        userManageService.suspendUser(id, type);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "정지 상태인 계정 정지 해제")
    @PutMapping("/unsuspend")
    public ResponseEntity<Void> unSuspendUser(@RequestParam Long id) {
        userManageService.unSuspendUser(id);
        return ResponseEntity.ok().build();
    }
}
