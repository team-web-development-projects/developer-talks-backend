package com.dtalks.dtalks.report.service;

import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.report.dto.ReportDetailRequestDto;
import com.dtalks.dtalks.report.entity.ReportedUser;
import com.dtalks.dtalks.report.enums.ResultType;
import com.dtalks.dtalks.report.repository.ReportedUserRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportUserServiceImpl implements ReportUserService {

    private final UserRepository userRepository;
    private final ReportedUserRepository reportedUserRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSource messageSource;

    @Override
    public void report(String nickname, ReportDetailRequestDto dto) {
        User reportUser = SecurityUtil.getUser();
        User reportedUser = userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "해당하는 사용자를 찾을 수 없습니다."));

        if (reportedUser.getIsActive()) {
            ReportedUser report = ReportedUser.builder()
                    .reportUser(reportUser)
                    .reportType(dto.getReportType())
                    .detail(dto.getDetail())
                    .processed(false)
                    .resultType(ResultType.WAIT)
                    .reportedUser(reportedUser)
                    .build();
            reportedUserRepository.save(report);

            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(null, null, reportedUser,
                    NotificationType.REPORTED, messageSource.getMessage("notification.reported", new Object[]{dto.getReportType()}, null)));
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "탈퇴한 사용자입니다.");
        }
    }
}
