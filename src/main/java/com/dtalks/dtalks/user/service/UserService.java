package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.base.dto.DocumentResponseDto;
import com.dtalks.dtalks.studyroom.enums.Skill;
import com.dtalks.dtalks.user.dto.*;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    SignUpResponseDto signUp(SignUpDto signUpDto);

    SignInResponseDto signIn(SignInDto signInDto);

    SignInResponseDto reSignIn(String refreshToken);

    DuplicateResponseDto useridDuplicated(String userid);

    DuplicateResponseDto nicknameDuplicated(String nickname);

    DocumentResponseDto userProfileImageUpLoad(MultipartFile file);

    ResponseEntity<Resource> getUserProfileImage();

    UserResponseDto userInfo();

    UserResponseDto updateUserDescription(String description);

    UserResponseDto updateUserSkills(List<Skill> skills);

    Page<RecentActivityDto> getRecentActivities(Pageable pageable);

    SignInResponseDto oAuthSignUp(OAuthSignUpDto oAuthSignUpDto);
}
