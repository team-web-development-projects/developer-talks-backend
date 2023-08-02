package com.dtalks.dtalks.user.repository;

import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserid(String userid);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);

    Page<User> findByStatusNot(ActiveStatus status, Pageable pageable);
    List<User> findByStatusAndModifiedDateLessThanEqual(ActiveStatus status, LocalDateTime unSuspendDate);
}
