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
    void signUp(SignUpDto signUpDto);

    SignInResponseDto oAuthSignUp(OAuthSignUpDto oAuthSignUpDto);

    SignInResponseDto signIn(SignInDto signInDto);

    SignInResponseDto adminSignIn(SignInDto signInDto);

    DuplicateResponseDto useridDuplicated(String userid);

    DuplicateResponseDto nicknameDuplicated(String nickname);

    DuplicateResponseDto emailDuplicated(String email);

    SignInResponseDto reSignIn(String refreshToken);

    UserResponseDto updateUserid(UseridDto useridDto);

    UserResponseDto updateNickname(UserNicknameDto userNicknameDto);

    void updatePassword(UserPasswordDto userPasswordDto);

    UserResponseDto updateEmail(UserEmailDto userEmailDto);

    UserResponseDto updateProfile(UserProfileRequestDto userProfileRequestDto);

    DocumentResponseDto updateUserProfileImage(MultipartFile multipartFile);

    DocumentResponseDto userProfileImageUpLoad(MultipartFile file);

    DocumentResponseDto getUserProfileImage();

    UserResponseDto userInfo();

    void updatePrivate(boolean status);

    Boolean getPrivateStatus(String id);

    void findUserid(String email);

    void findUserPassword(HttpServletRequest request, UserPasswordFindDto userPasswordFindDto);

    void quitUser(UserSimplePasswordDto passwordDto);
}
