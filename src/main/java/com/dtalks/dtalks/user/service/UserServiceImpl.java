package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.common.CommonResponse;
import com.dtalks.dtalks.user.config.JwtTokenProvider;
import com.dtalks.dtalks.user.dto.*;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

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
        User user = User.builder()
                .username(signUpDto.getUsername())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .email(signUpDto.getEmail())
                .nickname(signUpDto.getNickname())
                .roles(Collections.singletonList("ROLE_USER"))
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
        User user = userRepository.getByUsername(signInDto.getUsername());

        if(!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new RuntimeException();
        }

        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(user.getUsername()), user.getRoles()))
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

//    @Override
//    public UserResponseDto saveUser(UserDto userDto) {
//
//        User user = new User();
//        user.setUsername(userDto.getUsername());
//        user.setPassword(userDto.getPassword());
//        user.setEmail(userDto.getEmail());
//        user.setNickname(userDto.getNickname());
//        user.setCreateAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//
//        userRepository.save(user);
//
//        UserResponseDto userResponseDto = new UserResponseDto();
//        userResponseDto.setUsername(userDto.getUsername());
//        userResponseDto.setEmail(userDto.getEmail());
//        userResponseDto.setNickname(userDto.getNickname());
//
//        return userResponseDto;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) {
//        return userRepository.getByUsername(username);
//    }
}
