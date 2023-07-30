package com.dtalks.dtalks.admin.user.service;

import com.dtalks.dtalks.admin.user.dto.UserManageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManageService {
    Page<UserManageDto> searchAllUsersExceptQuit(Pageable pageable);
}
