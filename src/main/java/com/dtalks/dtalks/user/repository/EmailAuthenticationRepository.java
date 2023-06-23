package com.dtalks.dtalks.user.repository;

import com.dtalks.dtalks.user.entity.EmailAuthentication;
import org.springframework.data.repository.CrudRepository;

public interface EmailAuthenticationRepository extends CrudRepository<EmailAuthentication, String> {
}
