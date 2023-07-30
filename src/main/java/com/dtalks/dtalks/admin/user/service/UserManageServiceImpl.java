package com.dtalks.dtalks.admin.user.service;

import com.dtalks.dtalks.admin.user.dto.UserManageDto;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserManageDto> searchAllUsersExceptQuit(Pageable pageable) {
        Page<User> all = userRepository.findByStatusNot(ActiveStatus.QUIT, pageable);
        return all.map(UserManageDto::toDto);
    }
}
