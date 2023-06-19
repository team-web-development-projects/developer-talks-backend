package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.base.component.S3Uploader;
import com.dtalks.dtalks.base.dto.DocumentResponseDto;
import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.base.repository.DocumentRepository;
import com.dtalks.dtalks.base.validation.FileValidation;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.common.CommonResponse;
import com.dtalks.dtalks.user.dto.*;
import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActivityType;
import com.dtalks.dtalks.user.repository.ActivityRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final DocumentRepository documentRepository;
    private final ActivityRepository activityRepository;
    private final S3Uploader s3Uploader;
    private final String imagePath =  "profiles";

    @Override
    @Transactional
    public SignUpResponseDto signUp(SignUpDto signUpDto) {

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

        LOGGER.info("SERVICE signUp");
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

        User savedUser = userRepository.save(user);
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();

        if(!savedUser.getUsername().isEmpty()) {
            setSuccessResult(signUpResponseDto);
        }
        else {
            setFailResult(signUpResponseDto);
        }
        return signUpResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SignInResponseDto signIn(SignInDto signInDto) {
        LOGGER.info("SERVICE signIn");
        User user = findUser(signInDto.getUserid());

        UserTokenDto userTokenDto = UserTokenDto.toDto(user);

        if(!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new RuntimeException();
        }

        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .accessToken(tokenService.createAccessToken(userTokenDto))
                .refreshToken(tokenService.createRefreshToken(userTokenDto))
                .build();
        setSuccessResult(signInResponseDto);
        return signInResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateResponseDto useridDuplicated(String userid) {
        LOGGER.info("useridDuplicated 호출됨");
        Optional<User> user = userRepository.findByUserid(userid);
        DuplicateResponseDto duplicateResponseDto = new DuplicateResponseDto();
        if(user.isEmpty()) {
            duplicateResponseDto.setDuplicated(false);
        }
        else {
            duplicateResponseDto.setDuplicated(true);
        }
        return duplicateResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateResponseDto nicknameDuplicated(String nickname) {
        LOGGER.info("nicknameDuplicated 호출됨");
        Optional<User> user = userRepository.findByNickname(nickname);
        DuplicateResponseDto duplicateResponseDto = new DuplicateResponseDto();
        if(user.isEmpty()) {
            duplicateResponseDto.setDuplicated(false);
        }
        else {
            duplicateResponseDto.setDuplicated(true);
        }
        return duplicateResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateResponseDto emailDuplicated(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        DuplicateResponseDto duplicateResponseDto = new DuplicateResponseDto();
        if(user.isEmpty()) {
            duplicateResponseDto.setDuplicated(false);
        }
        else {
            duplicateResponseDto.setDuplicated(true);
        }
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
        SignInResponseDto signInResponseDto = new SignInResponseDto();
        if(refreshToken == null || !tokenService.validateToken(refreshToken)) {
            setFailResult(signInResponseDto);
            return signInResponseDto;
        }

        String email = tokenService.getEmailByToken(refreshToken);
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            setFailResult(signInResponseDto);
            return signInResponseDto;
        }
        UserTokenDto userTokenDto = UserTokenDto.toDto(user.get());

        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));
        setSuccessResult(signInResponseDto);

        return signInResponseDto;
    }

    @Override
    @Transactional
    public SignInResponseDto updateNickname(String nickname) {
        User user = SecurityUtil.getUser();
        user.setNickname(nickname);

        User savedUser = userRepository.save(user);
        UserTokenDto userTokenDto = UserTokenDto.toDto(user);
        SignInResponseDto signInResponseDto = new SignInResponseDto();

        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));
        setSuccessResult(signInResponseDto);
        return signInResponseDto;
    }

    @Override
    @Transactional
    public DocumentResponseDto userProfileImageUpLoad(MultipartFile file) {
        FileValidation.imageValidation(file.getOriginalFilename());
        String path = S3Uploader.createFilePath(file.getOriginalFilename(), imagePath);
        String url = s3Uploader.fileUpload(file, path);

        Document document = Document.builder()
                .inputName(file.getOriginalFilename())
                .path(path)
                .url(url).build();

        Document savedDocument = documentRepository.save(document);
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
    public UserResponseDto updateUserProfile(UserProfileRequestDto userProfileRequestDto) {
        User user = SecurityUtil.getUser();

        user.setDescription(userProfileRequestDto.getDescription());
        user.setSkills(userProfileRequestDto.getSkills());
        User savedUser = userRepository.save(user);

        UserResponseDto userResponseDto = UserResponseDto.toDto(savedUser);

        return userResponseDto;
    }

    @Override
    public DocumentResponseDto updateUserProfileImage(MultipartFile multipartFile) {
        FileValidation.imageValidation(multipartFile.getOriginalFilename());
        User user = SecurityUtil.getUser();
        Document document = user.getProfileImage();
        String path = S3Uploader.createFilePath(multipartFile.getOriginalFilename(), imagePath);
        String url = s3Uploader.fileUpload(multipartFile, path);

        if(document == null) {
            Document doc = Document.builder()
                    .inputName(multipartFile.getOriginalFilename())
                    .url(url)
                    .path(path)
                    .build();
            Document savedDocument = documentRepository.save(doc);
            user.setProfileImage(savedDocument);
            userRepository.save(user);
            return DocumentResponseDto.toDto(savedDocument);
        }

        s3Uploader.deleteFile(document.getPath());
        document.setInputName(multipartFile.getOriginalFilename());
        document.setUrl(url);
        document.setPath(path);

        Document savedDocument = documentRepository.save(document);
        return DocumentResponseDto.toDto(savedDocument);
    }

    @Override
    @Transactional
    public Page<RecentActivityDto> getRecentActivities(String nickname, Pageable pageable) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();
        if (user.getIsPrivate()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "비공개 설정으로 사용자의 최근활동 조회가 불가능합니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime goe = now.minusDays(30);

        Page<Activity> page = activityRepository.findByUserIdAndCreateDateBetween(user.getId(), goe, now, pageable);
        return page.map(p -> {
            Long id = null;
            Long subId = null;
            ActivityType type = p.getType();
            String title = "";
            String writer = "";
            switch (type) {
                case POST, COMMENT:
                    Post post = p.getPost();
                    if (post != null) {
                        id = post.getId();
                        title = post.getTitle();
                        writer = post.getUser().getNickname();
                    }
                    if (type.equals(ActivityType.COMMENT) && p.getComment() != null) {
                        subId = p.getComment().getId();
                    }
                    break;
                case QUESTION, ANSWER, ANSWER_SELECTED, SELECT_ANSWER:
                    Question question = p.getQuestion();
                    if (question != null) {
                        id = question.getId();
                        title = question.getTitle();
                        writer = question.getUser().getNickname();
                    }
                    if (p.getAnswer() != null) {
                        subId = p.getAnswer().getId();
                    }
                    break;
            }
            return RecentActivityDto.toDto(id, subId, type, title, writer, p.getCreateDate());
        });

    }

    @Override
    @Transactional
    public void updatePrivate(boolean status) {
        User user = SecurityUtil.getUser();
        user.setIsPrivate(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean getPrivateStatus(String id) {
        User user = findUser(id);
        return user.getIsPrivate();
    }


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
        String accessToken = tokenService.createAccessToken(userTokenDto);
        String refreshToken = tokenService.createRefreshToken(userTokenDto);

        SignInResponseDto signInResponseDto = new SignInResponseDto();
        signInResponseDto.setAccessToken(accessToken);
        signInResponseDto.setRefreshToken(refreshToken);
        setSuccessResult(signInResponseDto);

        return signInResponseDto;
    }

    @Override
    @Transactional
    public SignInResponseDto updateUserid(UseridDto useridDto) {
        User user = SecurityUtil.getUser();

        if(user.getRegistrationId() != null) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "사이트를 통해 회원가입한 유저만 아이디 변경이 가능합니다.");
        }

        user.setUserid(useridDto.getUserid());
        User savedUser = userRepository.save(user);

        SignInResponseDto signInResponseDto = new SignInResponseDto();
        setSuccessResult(signInResponseDto);
        UserTokenDto userTokenDto = UserTokenDto.toDto(savedUser);
        String accessToken = tokenService.createAccessToken(userTokenDto);
        String refreshToken = tokenService.createRefreshToken(userTokenDto);
        signInResponseDto.setAccessToken(accessToken);
        signInResponseDto.setRefreshToken(refreshToken);

        return signInResponseDto;
    }

    @Override
    @Transactional
    public SignInResponseDto updateUserPassword(UserPasswordDto userPasswordDto) {
        User user = SecurityUtil.getUser();

        if(user.getRegistrationId() != null) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "사이트를 통해 회원가입한 유저만 아이디 변경이 가능합니다.");
        }

        if(!passwordEncoder.matches(userPasswordDto.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "기존 비밀번호가 틀렸습니다.");
        }

        if(!userPasswordDto.getNewPassword().equals(userPasswordDto.getCheckNewPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(userPasswordDto.getNewPassword()));
        User savedUser = userRepository.save(user);

        SignInResponseDto signInResponseDto = new SignInResponseDto();
        setSuccessResult(signInResponseDto);
        UserTokenDto userTokenDto = UserTokenDto.toDto(savedUser);
        String accessToken = tokenService.createAccessToken(userTokenDto);
        String refreshToken = tokenService.createRefreshToken(userTokenDto);
        signInResponseDto.setAccessToken(accessToken);
        signInResponseDto.setRefreshToken(refreshToken);
        return signInResponseDto;
    }

    @Override
    @Transactional
    public SignInResponseDto updateUserEmail(UserEmailDto userEmailDto) {
        User user = SecurityUtil.getUser();

        if(user.getRegistrationId() != null) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "사이트를 통해 회원가입한 유저만 아이디 변경이 가능합니다.");
        }

        user.setEmail(userEmailDto.getEmail());
        User savedUser = userRepository.save(user);

        SignInResponseDto signInResponseDto = new SignInResponseDto();
        setSuccessResult(signInResponseDto);
        UserTokenDto userTokenDto = UserTokenDto.toDto(savedUser);
        String accessToken = tokenService.createAccessToken(userTokenDto);
        String refreshToken = tokenService.createRefreshToken(userTokenDto);
        signInResponseDto.setAccessToken(accessToken);
        signInResponseDto.setRefreshToken(refreshToken);
        return signInResponseDto;
    }

    private void setSuccessResult(SignUpResponseDto result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    private void setFailResult(SignUpResponseDto result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
    }

    @Transactional(readOnly = true)
    private User findUser(String userid) {
        Optional<User> user = userRepository.findByUserid(userid);
        if(user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "유저를 찾을 수 없습니다.");
        }
        return user.get();
    }
}
