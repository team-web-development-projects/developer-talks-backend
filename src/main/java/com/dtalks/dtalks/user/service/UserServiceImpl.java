package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.base.dto.DocumentResponseDto;
import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.base.repository.DocumentRepository;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.studyroom.enums.Skill;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.common.CommonResponse;
import com.dtalks.dtalks.user.dto.*;
import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActivityType;
import com.dtalks.dtalks.user.repository.ActivityRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public final UserRepository userRepository;
    public final TokenService tokenService;
    public PasswordEncoder passwordEncoder;
    public final DocumentRepository documentRepository;
    private final String imagePath =  new File("").getAbsolutePath() + "/files/profile/";

    private final ActivityRepository activityRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenService tokenService,
                           PasswordEncoder passwordEncoder, DocumentRepository documentRepository,
                           ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.documentRepository = documentRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    @Transactional
    public SignUpResponseDto signUp(SignUpDto signUpDto) {

        User userCheck = userRepository.getByUserid(signUpDto.getUserid());
        User userCheck2 = userRepository.getByEmail(signUpDto.getEmail());
        User userCheck3 = userRepository.getByNickname(signUpDto.getNickname());
        if(userCheck != null) {
            throw new CustomException(ErrorCode.USER_DUPLICATION_ERROR, "userid duplicated");
        }
        if(userCheck2 != null) {
            throw new CustomException(ErrorCode.USER_DUPLICATION_ERROR, "email duplicated");
        }
        if(userCheck3 != null) {
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
        User user = userRepository.getByUserid(signInDto.getUserid());

        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setEmail(user.getEmail());
        userTokenDto.setNickname(user.getNickname());
        userTokenDto.setUserid(user.getUserid());
        userTokenDto.setProvider(user.getRegistrationId());

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
        User user = userRepository.getByUserid(userid);
        DuplicateResponseDto duplicateResponseDto = new DuplicateResponseDto();
        if(user == null) {
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
        User user = userRepository.getByNickname(nickname);
        DuplicateResponseDto duplicateResponseDto = new DuplicateResponseDto();
        if(user == null) {
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
        User user = userRepository.getByEmail(email);

        if(user == null) {
            setFailResult(signInResponseDto);
            return signInResponseDto;
        }
        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setEmail(user.getEmail());
        userTokenDto.setUserid(user.getUserid());
        userTokenDto.setNickname(user.getNickname());
        userTokenDto.setProvider(user.getRegistrationId());
        signInResponseDto.setAccessToken(tokenService.createAccessToken(userTokenDto));
        signInResponseDto.setRefreshToken(tokenService.createRefreshToken(userTokenDto));
        setSuccessResult(signInResponseDto);

        return signInResponseDto;
    }

    @Override
    @Transactional
    public DocumentResponseDto userProfileImageUpLoad(MultipartFile file) {
        if(file == null) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND_ERROR, "파일이 올바르지 않습니다.");
        }

        String inputName = file.getOriginalFilename();
        String format = getImageFormat(inputName);
        if(!(format.equals("jpg") || format.equals("png"))) {
            throw new CustomException(ErrorCode.FILE_FORMAT_ERROR, "파일 형식이 올바르지 않습니다.");
        }

        String saveName = System.currentTimeMillis() + "." + format;
        Path savePath = Paths.get(imagePath + saveName);

        try {
            Files.write(savePath, file.getBytes());
        }
        catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e.toString());
        }

        Document document = new Document();
        document.setInputName(inputName);
        document.setStoreName(saveName);

        Document savedDocument = documentRepository.save(document);

        DocumentResponseDto documentResponseDto = new DocumentResponseDto();
        documentResponseDto.setId(savedDocument.getId());
        documentResponseDto.setName(savedDocument.getInputName());

        return documentResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto userInfo() {
        User user = userRepository.getByUserid(SecurityUtil.getCurrentUserId());

        UserResponseDto userResponseDto = UserResponseDto.toDto(user);

        return userResponseDto;
    }

    @Override
    @Transactional
    public UserResponseDto updateUserDescription(String description) {
        User user = userRepository.getByUserid(SecurityUtil.getCurrentUserId());

        user.setDescription(description);
        User savedUser = userRepository.save(user);

        UserResponseDto userResponseDto = UserResponseDto.toDto(savedUser);

        return userResponseDto;
    }

    @Override
    @Transactional
    public Page<RecentActivityDto> getRecentActivities(Pageable pageable) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime goe = now.minusDays(30);

        Page<Activity> page = activityRepository.findByUserIdAndCreateDateBetween(optionalUser.get().getId(), goe, now, pageable);
        return page.map(p -> {
            Long id = null;
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
                    break;
                case QUESTION, ANSWER, ANSWER_SELECTED, SELECT_ANSWER, CANCEL_SELECT_ANSWER:
                    Question question = p.getQuestion();
                    if (question != null) {
                        id = question.getId();
                        title = question.getTitle();
                        writer = question.getUser().getNickname();
                    }
                    break;
            }
            return RecentActivityDto.toDto(id, type, title, writer, p.getCreateDate());
        });

    }

    @Override
    @Transactional
    public UserResponseDto updateUserSkills(List<Skill> skills) {
        User user = userRepository.getByUserid(SecurityUtil.getCurrentUserId());
        user.setSkills(skills);
        User savedUser = userRepository.save(user);
        return UserResponseDto.toDto(savedUser);
    }

    @Override
    public UserResponseDto oAuthSignUp(OAuthSignUpDto oAuthSignUpDto) {
        User user = userRepository.getByUserid(SecurityUtil.getCurrentUserId());
        Optional<Document> optionalImage = documentRepository.findById(oAuthSignUpDto.getProfileImageId());

        if(optionalImage.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND_ERROR, "해당하는 이미지를 찾을 수 없습니다.");
        }

        user.setNickname(oAuthSignUpDto.getNickname());
        user.setSkills(oAuthSignUpDto.getSkills());
        user.setDescription(oAuthSignUpDto.getDescription());
        user.setProfileImage(optionalImage.get());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        return UserResponseDto.toDto(savedUser);
    }

    private String getImageFormat(String imageName) {
        String s[] = imageName.split("[.]");
        return s[s.length-1].toLowerCase();
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
}
