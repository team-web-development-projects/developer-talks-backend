package com.dtalks.dtalks.admin.report.service;

import com.dtalks.dtalks.admin.report.dto.ReportDetailDto;
import com.dtalks.dtalks.admin.report.dto.ReportedUserDto;
import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.report.entity.ReportedUser;
import com.dtalks.dtalks.report.enums.ResultType;
import com.dtalks.dtalks.report.repository.CustomReportedUserRepository;
import com.dtalks.dtalks.report.repository.ReportedUserRepository;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActiveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final CustomReportedUserRepository customReportedUserRepository;
    private final ReportedUserRepository reportedUserRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSource messageSource;

    @Override
    public Page<ReportedUserDto> searchAllNotProgressedUserReports(Pageable pageable) {
       return customReportedUserRepository.findDistinctByProcessed(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDetailDto> getAllNotProgressedReportsByUser(Long reportedUserId, Pageable pageable) {
        Page<ReportedUser> reportedUser = reportedUserRepository.findByProcessedFalseAndReportedUserId(reportedUserId, pageable);
        return reportedUser.map(ReportDetailDto::toDto);
    }

    @Override
    @Transactional
    public ResultType handleReport(Long reportedUserId, ResultType resultType) {
        List<ReportedUser> report = reportedUserRepository.findByProcessedFalseAndReportedUserIdAndCreateDateLessThan(reportedUserId, LocalDateTime.now());
        User reportedUser = report.get(0).getReportedUser();
        for (ReportedUser reported : report) {
            reported.reportProcessed();
            reported.updateResult(resultType);
        }

        String message = "";
        if (resultType.equals(ResultType.BAN)) {
            reportedUser.updateStatus(ActiveStatus.BAN);
            message = messageSource.getMessage("notification.reported.result.ban", null, null);
        } else if (resultType.equals(ResultType.SUSPENSION)) {
            reportedUser.updateStatus(ActiveStatus.SUSPENSION);
            message = messageSource.getMessage("notification.reported.result.suspension", null, null);
        }

        if (!resultType.equals(ResultType.NP)) {
            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(null, null, reportedUser,
                    NotificationType.REPORTED_RESULT, message));
        }
        return resultType;
    }

}
