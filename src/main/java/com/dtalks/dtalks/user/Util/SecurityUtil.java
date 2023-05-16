package com.dtalks.dtalks.user.Util;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.PermissionNotGrantedException;
import com.dtalks.dtalks.user.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class SecurityUtil {

    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtFilter 의 doFilter 에서 저장
    public static String getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        return authentication.getName();
    }

    public static void checkUserPermission(User user) {
        if(!user.getUserid().equals(getCurrentUserId())) {
            throw new PermissionNotGrantedException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "권한이 없습니다.");
        }
    }
}
