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

    SignInResponseDto updateNickname(String nickname);

    DuplicateResponseDto useridDuplicated(String userid);

    DuplicateResponseDto nicknameDuplicated(String nickname);

    DuplicateResponseDto emailDuplicated(String email);

    DocumentResponseDto userProfileImageUpLoad(MultipartFile file);

    DocumentResponseDto getUserProfileImage();

    DocumentResponseDto changeUserProfileImage(MultipartFile multipartFile);

    UserResponseDto userInfo();

    UserResponseDto updateUserProfile(UserProfileRequestDto userProfileRequestDto);

    Page<RecentActivityDto> getRecentActivities(String nickname, Pageable pageable);
    void updatePrivate(boolean status);
    Boolean getPrivateStatus(String id);

    SignInResponseDto oAuthSignUp(OAuthSignUpDto oAuthSignUpDto);
}
