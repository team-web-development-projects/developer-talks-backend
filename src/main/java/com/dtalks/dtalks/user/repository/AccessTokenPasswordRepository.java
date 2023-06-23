package com.dtalks.dtalks.user.repository;

import com.dtalks.dtalks.user.entity.AccessTokenPassword;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenPasswordRepository extends CrudRepository<AccessTokenPassword, String> {
}
