package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.dto.AccessTokenDto;
import jakarta.mail.internet.MimeMessage;

public interface EmailService {
    String sendEmailAuthenticationCode(String email) throws Exception;

    void checkEmailVerify(String code);

    AccessTokenDto checkEmailVerifyPassword(String code);

    void sendEmail(MimeMessage mimeMessage);

    MimeMessage createUseridMessage(String email, String userid);
}
