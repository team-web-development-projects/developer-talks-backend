package com.dtalks.dtalks.admin.user.service;

import com.dtalks.dtalks.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    Page<User> searchAllUsers(Pageable pageable);
}
