package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.base.dto.DocumentResponseDto;
import com.dtalks.dtalks.studyroom.enums.Skill;
import com.dtalks.dtalks.user.dto.*;
import jakarta.servlet.http.HttpServletRequest;
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

    SignInResponseDto updateNickname(UserNicknameDto userNicknameDto);

    DuplicateResponseDto useridDuplicated(String userid);

    DuplicateResponseDto nicknameDuplicated(String nickname);

    DuplicateResponseDto emailDuplicated(String email);

    DocumentResponseDto userProfileImageUpLoad(MultipartFile file);

    DocumentResponseDto getUserProfileImage();

    DocumentResponseDto updateUserProfileImage(MultipartFile multipartFile);

    UserResponseDto userInfo();

    UserResponseDto updateUserProfile(UserProfileRequestDto userProfileRequestDto);

    void updatePrivate(boolean status);

    Boolean getPrivateStatus(String id);

    SignInResponseDto oAuthSignUp(OAuthSignUpDto oAuthSignUpDto);

    SignInResponseDto updateUserid(UseridDto useridDto);

    SignInResponseDto updateUserPassword(UserPasswordDto userPasswordDto);

    SignInResponseDto updateUserEmail(UserEmailDto userEmailDto);

    void findUserid(String email);

    void findUserPassword(HttpServletRequest request, UserPasswordFindDto userPasswordFindDto);
}
