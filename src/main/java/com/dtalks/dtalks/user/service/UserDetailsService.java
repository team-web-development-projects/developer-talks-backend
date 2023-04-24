package com.dtalks.dtalks.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {
    UserDetails loadUserByUserid(String userid) throws UsernameNotFoundException;
}
