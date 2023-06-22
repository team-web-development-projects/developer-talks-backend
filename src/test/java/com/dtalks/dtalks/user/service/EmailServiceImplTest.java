package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.entity.EmailAuthentication;
import com.dtalks.dtalks.user.repository.EmailAuthenticationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceImplTest {

    @Autowired
    EmailService emailService;
    @Autowired
    EmailAuthenticationRepository emailAuthenticationRepository;

    @AfterEach
    void afterEach() {
        emailAuthenticationRepository.deleteAll();
    }

    @Test
    void sendEmailAuthenticationCode_success() throws Exception{
        // given
        String email = "dhdgn@naver.com";

        // when
        String code = emailService.sendEmailAuthenticationCode(email);

        // then
        emailService.checkEmailVerify(code);
    }
}