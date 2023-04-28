package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.UserDuplicateException;
import com.dtalks.dtalks.user.common.CommonResponse;
import com.dtalks.dtalks.user.config.JwtTokenProvider;
import com.dtalks.dtalks.user.dto.*;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public final UserRepository userRepository;
    public final JwtTokenProvider jwtTokenProvider;
    public PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SignUpResponseDto signUp(SignUpDto signUpDto) {

        User userCheck = userRepository.getByUserid(signUpDto.getUserid());
        User userCheck2 = userRepository.getByEmail(signUpDto.getEmail());
        User userCheck3 = userRepository.getByNickname(signUpDto.getNickname());
        if(userCheck != null) {
            throw new UserDuplicateException("userid duplicated", ErrorCode.USER_DUPLICATION_ERROR);
        }
        if(userCheck2 != null) {
            throw new UserDuplicateException("email duplicated", ErrorCode.USER_DUPLICATION_ERROR);
        }
        if(userCheck3 != null) {
            throw new UserDuplicateException("nickname duplicated", ErrorCode.USER_DUPLICATION_ERROR);
        }

        LOGGER.info("SERVICE signUp");
        User user = User.builder()
                .userid(signUpDto.getUserid())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .email(signUpDto.getEmail())
                .nickname(signUpDto.getNickname())
                .roles(Collections.singletonList("USER"))
                .createAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

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
    public SignInResponseDto signIn(SignInDto signInDto) {
        LOGGER.info("SERVICE signIn");
        User user = userRepository.getByUserid(signInDto.getUserid());

        if(!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new RuntimeException();
        }

        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(user.getEmail()), user.getRoles()))
                .build();
        setSuccessResult(signInResponseDto);
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
}
