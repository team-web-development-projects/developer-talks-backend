package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.dto.AccessTokenDto;
import com.dtalks.dtalks.user.dto.UserTokenDto;
import com.dtalks.dtalks.user.entity.AccessTokenPassword;
import com.dtalks.dtalks.user.entity.EmailAuthentication;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.AccessTokenPasswordRepository;
import com.dtalks.dtalks.user.repository.EmailAuthenticationRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final AccessTokenPasswordRepository accessTokenPasswordRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public String sendEmailAuthenticationCode(String email) {
        try {
            String code = createCode();
            MimeMessage mimeMessage = createMessage(email, code);
            javaMailSender.send(mimeMessage);
            EmailAuthentication emailAuthentication = new EmailAuthentication(code, email, LocalDateTime.now());
            emailAuthenticationRepository.save(emailAuthentication);
            return code;
        }
        catch (MessagingException e) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "이메일 발송에 실패하였습니다.");
        }
        catch (UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "이메일 발송에 실패하였습니다.");
        }
    }

    @Override
    public void checkEmailVerify(String code) {
        Optional<EmailAuthentication> optionalEmailAuthentication = emailAuthenticationRepository.findById(code);
        if(optionalEmailAuthentication.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "인증번호가 틀렸습니다.");
        }
    }

    @Override
    public AccessTokenDto checkEmailVerifyPassword(String code) {
        Optional<EmailAuthentication> optionalEmailAuthentication = emailAuthenticationRepository.findById(code);
        if(optionalEmailAuthentication.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "인증번호가 틀렸습니다.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(optionalEmailAuthentication.get().getEmail());
        if(optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "존재하지 않는 유저입니다.");
        }


        UserTokenDto userTokenDto = UserTokenDto.toDto(optionalUser.get());
        String accessToken = tokenService.createAccessToken(userTokenDto);
        AccessTokenDto accessTokenDto = new AccessTokenDto();
        accessTokenDto.setAccessToken(accessToken);

        AccessTokenPassword accessTokenPassword = new AccessTokenPassword(accessToken, LocalDateTime.now());
        accessTokenPasswordRepository.save(accessTokenPassword);

        return accessTokenDto;
    }

    @Override
    public void sendEmail(MimeMessage mimeMessage) {
        javaMailSender.send(mimeMessage);
    }

    @Override
    public MimeMessage createUseridMessage(String email, String userid) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            mimeMessage.addRecipients(Message.RecipientType.TO, email);
            mimeMessage.setSubject("d-talks 아이디 찾기");

            StringBuffer message = new StringBuffer();
            message.append("<div style='margin:20px;'>");
            message.append("<h1> 안녕하세요 d-talks 입니다. </h1>");
            message.append("<br>");
            message.append("<p>회원님의 아이디는 다음과 같습니다.<p>");
            message.append("<br>");
            message.append("감사합니다.");
            message.append("<div align='center' style='border:1px solid black; font-family:verdana';>");
            message.append("<h3 style='color:blue;'>회원님의 아이디 입니다.</h3>");
            message.append("<div style='font-size:130%'>");
            message.append(userid + "</strong><div><br/> ");
            message.append("</div>");

            mimeMessage.setText(message.toString(), "utf-8", "html");
            mimeMessage.setFrom(new InternetAddress("rladydgn7575@gmail.com", "d-talks"));

            return mimeMessage;
        }
        catch (MessagingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "이메일 발송 실패");
        }
        catch (UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "이메일 발송 실패");
        }
    }

    private MimeMessage createMessage(String email, String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        mimeMessage.addRecipients(Message.RecipientType.TO, email);
        mimeMessage.setSubject("d-talks 이메일 인증");

        StringBuffer message = new StringBuffer();
        message.append("<div style='margin:20px;'>");
        message.append("<h1> 안녕하세요 d-talks 입니다. </h1>");
        message.append("<br>");
        message.append("<p>아래 코드를 복사해 입력해주세요<p>");
        message.append("<br>");
        message.append("감사합니다.");
        message.append("<div align='center' style='border:1px solid black; font-family:verdana';>");
        message.append("<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>");
        message.append("<div style='font-size:130%'>");
        message.append(code + "</strong><div><br/> ");
        message.append("</div>");

        mimeMessage.setText(message.toString(), "utf-8", "html");
        mimeMessage.setFrom(new InternetAddress("rladydgn7575@gmail.com", "d-talks"));

        return mimeMessage;
    }

    private String createCode() {
        StringBuffer code = new StringBuffer();
        Random random = new Random();

        for(int i=0; i<8; i++) {
            int idx = random.nextInt(3);

            switch (idx) {
                case 0:
                    code.append((char)((int)(random.nextInt(26)+97)));
                    break;
                case 1:
                    code.append((char) ((int) (random.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    code.append((random.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return code.toString();
    }
}
