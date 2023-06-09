//package com.dtalks.dtalks.user.service;
//
//import com.dtalks.dtalks.base.component.S3Uploader;
//import com.dtalks.dtalks.base.repository.DocumentRepository;
//import com.dtalks.dtalks.user.common.CommonResponse;
//import com.dtalks.dtalks.user.dto.SignUpDto;
//import com.dtalks.dtalks.user.dto.SignUpResponseDto;
//import com.dtalks.dtalks.user.entity.User;
//import com.dtalks.dtalks.user.repository.ActivityRepository;
//import com.dtalks.dtalks.user.repository.UserRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.AdditionalAnswers.returnsFirstArg;
//import static org.mockito.Mockito.verify;
//
//class UserServiceImplTest {
//
//    UserRepository userRepository = Mockito.mock(UserRepository.class);
//    TokenService tokenService = Mockito.mock(TokenService.class);
//    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
//    DocumentRepository documentRepository = Mockito.mock(DocumentRepository.class);
//    ActivityRepository activityRepository = Mockito.mock(ActivityRepository.class);
//    S3Uploader s3Uploader = Mockito.mock(S3Uploader.class);
//    UserServiceImpl userService;
//
//    @BeforeEach
//    public void setUpTest() {
//        userService = new UserServiceImpl(userRepository, tokenService,
//                passwordEncoder, documentRepository, activityRepository,
//                s3Uploader);
//    }
//    @Test
//    void signUpSuccess() {
//        Mockito.when(userRepository.save(any(User.class)))
//                .then(returnsFirstArg());
//
//        SignUpDto signUpDto = new SignUpDto();
//        signUpDto.setEmail("test@naver.com");
//        signUpDto.setPassword("password1!");
//        signUpDto.setUserid("test1");
//        signUpDto.setNickname("nickname");
//
//        SignUpResponseDto signUpResponseDto = userService.signUp(signUpDto);
//
//        Assertions.assertEquals(signUpResponseDto.getMsg(), CommonResponse.SUCCESS.getMsg());
//        Assertions.assertEquals(signUpResponseDto.getCode(), CommonResponse.SUCCESS.getCode());
//
//        // userRepository.save() 메서드가 호출 되었는지 확인
//        verify(userRepository).save(any());
//    }
//}