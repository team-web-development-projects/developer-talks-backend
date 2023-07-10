package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.base.component.S3Uploader;
import com.dtalks.dtalks.base.dto.DocumentResponseDto;
import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.base.repository.DocumentRepository;
import com.dtalks.dtalks.base.validation.FileValidation;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.common.CommonResponse;
import com.dtalks.dtalks.user.dto.*;
import com.dtalks.dtalks.user.entity.AccessTokenPassword;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.AccessTokenPasswordRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final DocumentRepository documentRepository;
    private final S3Uploader s3Uploader;
    private final EmailService emailService;
    private final AccessTokenPasswordRepository accessTokenPasswordRepository;
    private final String imagePath =  "profiles";

    @Override
    @Transactional
    public SignInResponseDto oAuthSignUp(OAuthSignUpDto oAuthSignUpDto) {
        User user = SecurityUtil.getUser();
        Optional<Document> optionalImage = documentRepository.findById(oAuthSignUpDto.getProfileImageId());

        if(!optionalImage.isEmpty()) {
            user.setProfileImage(optionalImage.get());
        }

        user.setNickname(oAuthSignUpDto.getNickname());
        user.setSkills(oAuthSignUpDto.getSkills());
        user.setDescription(oAuthSignUpDto.getDescription());
        user.setIsActive(true);
        user.setIsPrivate(false);

        User savedUser = userRepository.save(user);

        UserTokenDto userTokenDto = UserTokenDto.toDto(savedUser);
        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));

        return signInResponseDto;
    }

    @Override
    @Transactional
    public void signUp(SignUpDto signUpDto) {
        log.info("회원가입");
        userValidation(signUpDto);

        User user = User.builder()
                .userid(signUpDto.getUserid())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .email(signUpDto.getEmail())
                .nickname(signUpDto.getNickname())
                .skills(signUpDto.getSkills())
                .description(signUpDto.getDescription())
                .roles(Collections.singletonList("USER"))
                .isActive(true)
                .isPrivate(false)
                .build();

        Optional<Document> document = documentRepository.findById(signUpDto.getProfileImageId());
        if(!document.isEmpty()) {
            user.setProfileImage(document.get());
        }

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public SignInResponseDto signIn(SignInDto signInDto) {
        log.info("로그인");
        User user = findUser(signInDto.getUserid());

        UserTokenDto userTokenDto = UserTokenDto.toDto(user);
        log.info(signInDto.getPassword() + " " + user.getPassword());
        passwordValidation(signInDto.getPassword(), user.getPassword());

        if(!user.getIsActive())
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "비활성화된 계정입니다.");

        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .accessToken(tokenService.createAccessToken(userTokenDto))
                .refreshToken(tokenService.createRefreshToken(userTokenDto))
                .build();
        return signInResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateResponseDto useridDuplicated(String userid) {
        log.info("userid 중복체크");
        Optional<User> user = userRepository.findByUserid(userid);
        DuplicateResponseDto duplicateResponseDto = new DuplicateResponseDto(userDuplicated(user));
        return duplicateResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateResponseDto nicknameDuplicated(String nickname) {
        log.info("nickname 중복체크");
        Optional<User> user = userRepository.findByNickname(nickname);
        DuplicateResponseDto duplicateResponseDto = new DuplicateResponseDto(userDuplicated(user));
        return duplicateResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateResponseDto emailDuplicated(String email) {
        log.info("enail 중복체크");
        Optional<User> user = userRepository.findByEmail(email);
        DuplicateResponseDto duplicateResponseDto = new DuplicateResponseDto(userDuplicated(user));
        return duplicateResponseDto;
    }
    /*
    reSignIn은 토큰 재발급 서비스 입니다.
    oauth 로그인과, 일반 로그인 모두 토큰 재발급을 가능하게 하기 위해
    signIn 메서드와 구분하여 만들었습니다.
    */

    @Override
    @Transactional(readOnly = true)
    public SignInResponseDto reSignIn(String refreshToken) {
        // 토큰값 검증
        tokenService.validateToken(refreshToken);
        if(!tokenService.getAuthentication(refreshToken).isAuthenticated()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "유효하지 않은 코드입니다.");
        }

        User user = userRepository.findByEmail(tokenService.getEmailByToken(refreshToken)).get();

        UserTokenDto userTokenDto = UserTokenDto.toDto(user);
        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));

        return signInResponseDto;
    }

    @Override
    @Transactional
    public SignInResponseDto updateUserid(UseridDto useridDto) {
        User user = SecurityUtil.getUser();

        if(user.getRegistrationId() != null) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "구글 로그인 유저는 아이디 변경이 불가능합니다.");
        }

        Optional<User> optionalUser = userRepository.findByUserid(useridDto.getUserid());
        if(!optionalUser.isEmpty())
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "이미 존재하는 아이디입니다.");

        user.setUserid(useridDto.getUserid());
        User savedUser = userRepository.save(user);

        UserTokenDto userTokenDto = UserTokenDto.toDto(savedUser);
        SignInResponseDto signInResponseDto = new SignInResponseDto();

        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));
        return signInResponseDto;
    }

    @Override
    @Transactional
    public SignInResponseDto updateNickname(UserNicknameDto userNicknameDto) {
        User user = SecurityUtil.getUser();
        Optional<User> optionalUser = userRepository.findByNickname(userNicknameDto.getNickname());
        if(!optionalUser.isEmpty())
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "이미 존재하는 닉네임입니다.");

        user.setNickname(userNicknameDto.getNickname());
        User savedUser = userRepository.save(user);

        UserTokenDto userTokenDto = UserTokenDto.toDto(savedUser);
        SignInResponseDto signInResponseDto = new SignInResponseDto();

        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));
        return signInResponseDto;
    }

    @Override
    @Transactional
    public SignInResponseDto updatePassword(UserPasswordDto userPasswordDto) {
        User user = SecurityUtil.getUser();

        if(user.getRegistrationId() != null) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "구글 로그인 유저는 아이디 변경이 불가능합니다.");
        }

        if(!passwordEncoder.matches(userPasswordDto.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "기존 비밀번호가 틀렸습니다.");
        }

        if(!userPasswordDto.getNewPassword().equals(userPasswordDto.getCheckNewPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(userPasswordDto.getNewPassword()));
        User savedUser = userRepository.save(user);

        UserTokenDto userTokenDto = UserTokenDto.toDto(savedUser);
        SignInResponseDto signInResponseDto = new SignInResponseDto();

        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));
        return signInResponseDto;
    }

    @Override
    @Transactional
    public SignInResponseDto updateEmail(UserEmailDto userEmailDto) {
        User user = SecurityUtil.getUser();

        if(user.getRegistrationId() != null) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "구글 로그인 유저는 아이디 변경이 불가능합니다.");
        }

        user.setEmail(userEmailDto.getEmail());
        User savedUser = userRepository.save(user);

        UserTokenDto userTokenDto = UserTokenDto.toDto(savedUser);
        SignInResponseDto signInResponseDto = new SignInResponseDto();

        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));
        return signInResponseDto;
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(UserProfileRequestDto userProfileRequestDto) {
        User user = SecurityUtil.getUser();

        user.setDescription(userProfileRequestDto.getDescription());
        user.setSkills(userProfileRequestDto.getSkills());
        User savedUser = userRepository.save(user);

        UserResponseDto userResponseDto = UserResponseDto.toDto(savedUser);

        return userResponseDto;
    }

    @Override
    @Transactional
    public DocumentResponseDto updateUserProfileImage(MultipartFile multipartFile) {
        User user = SecurityUtil.getUser();
        Document document = user.getProfileImage();

        if(document != null) {
            s3Uploader.deleteFile(document.getPath());
            documentRepository.delete(document);
        }

        Document savedDocument = createProfileImage(multipartFile);
        user.setProfileImage(savedDocument);
        userRepository.save(user);
        return DocumentResponseDto.toDto(savedDocument);
    }

    @Override
    @Transactional
    public DocumentResponseDto userProfileImageUpLoad(MultipartFile file) {
        Document savedDocument = createProfileImage(file);
        return DocumentResponseDto.toDto(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponseDto getUserProfileImage() {
        User user = SecurityUtil.getUser();
        Document document = user.getProfileImage();
        if(document == null) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND_ERROR, "프로필 이미지가 존재하지 않습니다.");
        }

        return DocumentResponseDto.toDto(document);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto userInfo() {
        User user = SecurityUtil.getUser();

        UserResponseDto userResponseDto = UserResponseDto.toDto(user);

        return userResponseDto;
    }

    @Override
    @Transactional
    public void updatePrivate(boolean status) {
        User user = userRepository.findByUserid(SecurityUtil.getCurrentUserId()).get();
        user.setIsPrivate(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean getPrivateStatus(String id) {
        User user = findUser(id);
        return user.getIsPrivate();
    }


    @Override
    @Transactional(readOnly = true)
    public void findUserid(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "가입되지 않은 이메일입니다.");
        }

        User user = optionalUser.get();

        MimeMessage mimeMessage = emailService.createUseridMessage(user.getEmail(), user.getUserid());
        emailService.sendEmail(mimeMessage);
    }

    /*
    이메일 인증을 통해 토큰을 발급하여 redis에 저장된 토큰을 확인한다. 5분 이후 만료 된다.
    인증이 완료되면 비밀번호를 변경할 수 있게 한다.
     */
    @Override
    @Transactional
    public void findUserPassword(HttpServletRequest httpServletRequest, UserPasswordFindDto userPasswordFindDto) {
        Optional<AccessTokenPassword> optionalAccessTokenPassword = accessTokenPasswordRepository.findById(tokenService.resolveToken(httpServletRequest));
        if(optionalAccessTokenPassword.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "인증되지 않은 토큰입니다.");
        }

        User user = SecurityUtil.getUser();
        user.setPassword(passwordEncoder.encode(userPasswordFindDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void quitUser(UserSimplePasswordDto passwordDto) {
        User user = SecurityUtil.getUser();
        if(!passwordEncoder.matches(passwordDto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "비밀번호가 올바르지 않습니다.");
        user.setEmail(null);
        user.setUserid(null);
        user.setNickname("(알수없음)");
        user.setIsActive(false);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    private User findUser(String userid) {
        Optional<User> user = userRepository.findByUserid(userid);
        if(user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "유저를 찾을 수 없습니다.");
        }
        return user.get();
    }

    private void userValidation(SignUpDto signUpDto) {
        Optional<User> userCheck = userRepository.findByUserid(signUpDto.getUserid());
        Optional<User> userCheck2 = userRepository.findByEmail(signUpDto.getEmail());
        Optional<User> userCheck3 = userRepository.findByNickname(signUpDto.getNickname());
        if(userCheck.isPresent()) {
            throw new CustomException(ErrorCode.USER_DUPLICATION_ERROR, "userid duplicated");
        }
        if(userCheck2.isPresent()) {
            throw new CustomException(ErrorCode.USER_DUPLICATION_ERROR, "email duplicated");
        }
        if(userCheck3.isPresent()) {
            throw new CustomException(ErrorCode.USER_DUPLICATION_ERROR, "nickname duplicated");
        }
    }

    private void passwordValidation(String inputPassword, String originalPassword) {
        if(!passwordEncoder.matches(inputPassword, originalPassword)) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "비밀번호가 일치하지 않습니다.");
        }
    }

    private boolean userDuplicated(Optional<User> user) {
        if(user.isEmpty())
            return false;
        return true;
    }

    private Document createProfileImage(MultipartFile file) {
        FileValidation.imageValidation(file.getOriginalFilename());
        String path = S3Uploader.createFilePath(file.getOriginalFilename(), imagePath);
        String url = s3Uploader.fileUpload(file, path);

        Document document = Document.builder()
                .inputName(file.getOriginalFilename())
                .path(path)
                .url(url).build();

        Document savedDocument = documentRepository.save(document);
        return savedDocument;
    }
}
