package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    public final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUserid(String userid) {
        Optional<User> user = userRepository.findByUserid(userid);
        if(user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "유저를 찾을 수 없습니다.");
        }
        return user.get();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "유저를 찾을 수 없습니다.");
        }
        return user.get();
    }
}
