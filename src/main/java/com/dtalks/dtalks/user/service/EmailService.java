package com.dtalks.dtalks.user.service;

public interface EmailService {
    String sendEmailAuthenticationCode(String email) throws Exception;
}
