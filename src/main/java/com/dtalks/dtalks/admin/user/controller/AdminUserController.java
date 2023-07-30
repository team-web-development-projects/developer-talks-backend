package com.dtalks.dtalks.admin.user.controller;

import com.dtalks.dtalks.admin.user.dto.UserManageDto;
import com.dtalks.dtalks.admin.user.service.UserManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class AdminUserController {

    private final UserManageService userManageService;

    @Operation(summary = "사용자 전체 조회 (탈퇴 사용자 제외)", parameters = {
            @Parameter(name = "pageable", description = "페이지, size=10, sort=id, desc 적용 중"),
    })
    @GetMapping
    public ResponseEntity<Page<UserManageDto>> searchAllUsersExceptQuit(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userManageService.searchAllUsersExceptQuit(pageable));
    }
}
