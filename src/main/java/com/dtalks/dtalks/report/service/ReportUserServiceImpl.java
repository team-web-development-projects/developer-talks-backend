package com.dtalks.dtalks.report.service;

import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.report.dto.ReportDetailRequestDto;
import com.dtalks.dtalks.report.entity.ReportedUser;
import com.dtalks.dtalks.report.enums.ReportType;
import com.dtalks.dtalks.report.enums.ResultType;
import com.dtalks.dtalks.report.repository.ReportedUserRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportUserServiceImpl implements ReportUserService {

    private final UserRepository userRepository;
    private final ReportedUserRepository reportedUserRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public void report(String nickname, ReportDetailRequestDto dto) {
        User reportUser = SecurityUtil.getUser();
        User reportedUser = userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "해당하는 사용자를 찾을 수 없습니다."));

        if (!reportedUser.getIsActive()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "탈퇴한 사용자입니다.");
        } else if (reportedUser.getStatus() != ActiveStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ACCEPTED_BUT_ALREADY_EXISTS, "신고로 인해 정지된 계정입니다.");
        }

        boolean reportExists = reportedUserRepository.existsByDtypeAndReportUserIdAndReportedUserIdAndProcessed("USER", reportUser.getId(), reportedUser.getId(), false);
        if (reportExists) {
            throw new CustomException(ErrorCode.ACCEPTED_BUT_ALREADY_EXISTS, "해당 사용자에 대해 처리되지 않은 신고 접수 내역이 존재합니다.");
        }

        ReportedUser report = ReportedUser.builder()
                .reportUser(reportUser)
                .reportType(dto.getReportType())
                .detail(dto.getDetail())
                .processed(false)
                .resultType(ResultType.WAIT)
                .reportedUser(reportedUser)
                .build();
        reportedUserRepository.save(report);

        String type = dto.getReportType().equals(ReportType.OTHER) ? "기타" : "욕설";
        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(null, null, reportedUser,
                NotificationType.REPORTED, messageSource.getMessage("notification.reported", new Object[]{type}, null)));
    }
}
