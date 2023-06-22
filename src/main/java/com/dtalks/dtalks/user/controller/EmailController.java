package com.dtalks.dtalks.user.controller;

import com.dtalks.dtalks.user.dto.AccessTokenDto;
import com.dtalks.dtalks.user.dto.TimerDto;
import com.dtalks.dtalks.user.dto.UserEmailDto;
import com.dtalks.dtalks.user.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "이메일 인증번호 발송")
    @PostMapping("/verify")
    public ResponseEntity<TimerDto> sendEmailVerify(@RequestBody UserEmailDto userEmailDto) throws Exception{
        LOGGER.info("emailVerify 호출됨");
        return ResponseEntity.ok(emailService.sendEmailAuthenticationCode(userEmailDto.getEmail()));
    }

    @Operation(summary = "이메일 인증번호 확인")
    @GetMapping("/verify")
    public ResponseEntity emailVerify(@RequestParam String code) {
        emailService.checkEmailVerify(code);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 찾기를 위한 이메일 인증번호 확인")
    @GetMapping("/password/verify")
    public ResponseEntity<AccessTokenDto> passwordEmailVerify(@RequestParam String code) {
        return ResponseEntity.ok(emailService.checkEmailVerifyPassword(code));
    }
}
