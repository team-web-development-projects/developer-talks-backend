package com.dtalks.dtalks.user.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public String sendEmailAuthenticationCode(String email) throws MessagingException, UnsupportedEncodingException, MailException {
        String code = createCode();
        MimeMessage mimeMessage = createMessage(email, code);
        javaMailSender.send(mimeMessage);

        return code;
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
