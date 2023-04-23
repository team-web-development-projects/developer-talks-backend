package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.dto.*;
import com.dtalks.dtalks.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    SignUpResponseDto signUp(SignUpDto signUpDto);
    SignInResponseDto signIn(SignInDto signInDto);
}
