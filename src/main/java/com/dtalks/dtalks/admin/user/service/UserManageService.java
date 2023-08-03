package com.dtalks.dtalks.admin.user.service;

import com.dtalks.dtalks.admin.user.dto.UserManageDto;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManageService {
    Page<UserManageDto> searchAllUsersExceptQuit(Pageable pageable, ActiveStatus status);
}
