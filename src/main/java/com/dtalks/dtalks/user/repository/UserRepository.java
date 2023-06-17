package com.dtalks.dtalks.user.repository;

import com.dtalks.dtalks.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserid(String userid);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
}
