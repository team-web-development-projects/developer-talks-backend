package com.dtalks.dtalks.admin.user.service;

import com.dtalks.dtalks.admin.user.dto.UserInfoChangeRequestDto;
import com.dtalks.dtalks.admin.user.dto.UserManageDto;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import com.dtalks.dtalks.user.repository.UserRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public Page<UserManageDto> searchAllUsersExceptQuit(
            @PageableDefault(size = 10, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable, ActiveStatus status) {
        Page<User> all;
        if (status == null) {
            all = userRepository.findByStatusNot(ActiveStatus.QUIT, pageable);
        } else {
            all = userRepository.findByStatus(status, pageable);
        }
        return all.map(UserManageDto::toDto);
    }

    @Override
    @Transactional
    public UserManageDto updateUserInfo(Long id, UserInfoChangeRequestDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "사용자를 찾을 수 없습니다."));
        user.updateNickname(dto.getNickname());
        user.updateEmail(dto.getEmail());

        return UserManageDto.toDto(user);
    }

    @Override
    @Transactional
    public void updateUserPassword(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "사용자를 찾을 수 없습니다."));
        String password = createCode();
        user.updatePassword(passwordEncoder.encode(password));
        javaMailSender.send(createPasswordChangingMessage(user.getEmail(), password));
    }

    @Override
    @Transactional
    public void suspendUser(Long id, ActiveStatus type) {
        if (type.equals(ActiveStatus.ACTIVE) || type.equals(ActiveStatus.QUIT)) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "일시 정지 또는 영구 정지 타입만 가능합니다.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "해당 사용자를 찾을 수 없습니다."));
        if (user.getStatus() != ActiveStatus.ACTIVE) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "정지가 불가능한 상태입니다.");
        }

        user.updateStatus(type);
        String message;
        if (type.equals(ActiveStatus.SUSPENSION)) {
            message = "관리자에 의해 계정이 일시 정지되었습니다. 1주 후 활동이 가능합니다.";
        } else {
            message = "관리자에 의해 계정이 영구 정지되었습니다. 이 계정으로 활동이 불가능합니다.";
        }
        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(null, null, user,
                NotificationType.ACCOUNT_SUSPEND, message));
    }

    @Override
    @Transactional
    public void unSuspendUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "해당 사용자를 찾을 수 없습니다."));
        ActiveStatus status = user.getStatus();
        if (status == ActiveStatus.ACTIVE || status == ActiveStatus.QUIT) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "계정이 정지 상태가 아닙니다.");
        }
        user.setStatus(ActiveStatus.ACTIVE);
        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(null, null, user,
                NotificationType.ACCOUNT_UNSUSPEND, "관리자에 의해 계정 정지가 해제되었습니다. 활동이 가능합니다."));
    }

    public MimeMessage createPasswordChangingMessage(String email, String password) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            mimeMessage.addRecipients(Message.RecipientType.TO, email);
            mimeMessage.setSubject("d-talks 임시 비밀번호 안내");

            StringBuffer message = new StringBuffer();
            message.append("<div style='margin:20px;'>");
            message.append("<h1> 안녕하세요 d-talks 입니다. </h1>");
            message.append("<br>");
            message.append("<p>회원님의 임시 비밀번호는 다음과 같습니다. 로그인 후 새 비밀번호를 설정해주시기 바랍니다.<p>");
            message.append("<br>");
            message.append("<div align='center' style='border:1px solid black; font-family:verdana';>");
            message.append("<div style='font-size:130%'>");
            message.append(password + "</strong><div><br/> ");
            message.append("</div>");

            mimeMessage.setText(message.toString(), "utf-8", "html");
            mimeMessage.setFrom(new InternetAddress("rladydgn7575@gmail.com", "d-talks"));

            return mimeMessage;
        }
        catch (MessagingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "메시지 생성 에러 실패");
        }
        catch (UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "메시지 인코딩 실패");
        }
    }

    private String createCode() {
        StringBuffer code = new StringBuffer();
        Random random = new Random();

        for(int i=0; i<8; i++) {
            int idx = random.nextInt(4);

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
                case 3:
                    //특수문자 (아스키 코드값 : 33 ~ 47)
                    code.append((char)(random.nextInt(15)+33));
                    break;
            }
        }
        return code.toString();
    }
}
