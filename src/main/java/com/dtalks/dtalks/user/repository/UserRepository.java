package com.dtalks.dtalks.user.repository;

import com.dtalks.dtalks.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getByUsername(String username);
}
