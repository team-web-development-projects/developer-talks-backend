package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.base.dto.DocumentResponseDto;
import com.dtalks.dtalks.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    SignUpResponseDto signUp(SignUpDto signUpDto);

    SignInResponseDto signIn(SignInDto signInDto);

    SignInResponseDto reSignIn(String refreshToken);

    DuplicateResponseDto useridDuplicated(String userid);

    DuplicateResponseDto nicknameDuplicated(String nickname);

    DocumentResponseDto userProfileImageUpLoad(MultipartFile file);

    UserResponseDto userInfo();

    Page<RecentActivityDto> getRecentActivities(Pageable pageable);
}
