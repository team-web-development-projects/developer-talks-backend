package com.dtalks.dtalks.user.controller;

import com.dtalks.dtalks.user.dto.EmailVerifyResponseDto;
import com.dtalks.dtalks.user.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    EmailService emailService;
    private final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/verify")
    public ResponseEntity<EmailVerifyResponseDto> emailVerify(@RequestParam String email) throws Exception{
        LOGGER.info("emailVerify 호출됨");
        String code = emailService.sendEmailAuthenticationCode(email);
        EmailVerifyResponseDto emailVerifyResponseDto = new EmailVerifyResponseDto();
        emailVerifyResponseDto.setEmail(email);
        emailVerifyResponseDto.setCode(code);
        return ResponseEntity.ok(emailVerifyResponseDto);
    }
}
