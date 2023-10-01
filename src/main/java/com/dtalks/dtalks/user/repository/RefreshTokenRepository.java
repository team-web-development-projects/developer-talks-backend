package com.dtalks.dtalks.user.repository;

import com.dtalks.dtalks.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
