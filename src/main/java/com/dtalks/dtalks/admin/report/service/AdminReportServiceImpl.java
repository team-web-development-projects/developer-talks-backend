package com.dtalks.dtalks.admin.report.service;

import com.dtalks.dtalks.admin.report.dto.ReportDetailDto;
import com.dtalks.dtalks.admin.report.dto.ReportedPostDto;
import com.dtalks.dtalks.admin.report.dto.ReportedUserDto;
import com.dtalks.dtalks.admin.report.enums.DType;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.report.entity.ReportedPost;
import com.dtalks.dtalks.report.entity.ReportedUser;
import com.dtalks.dtalks.report.enums.ResultType;
import com.dtalks.dtalks.report.repository.CustomReportRepository;
import com.dtalks.dtalks.report.repository.ReportedPostRepository;
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

    private final CustomReportRepository customReportRepository;
    private final ReportedUserRepository reportedUserRepository;
    private final ReportedPostRepository reportedPostRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSource messageSource;

    @Override
    public Page<ReportedUserDto> searchAllNotProgressedUserReports(Pageable pageable) {
       return customReportRepository.findDistinctReportedUserByProcessed(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDetailDto> getAllNotProgressedReportsByUser(Long reportedUserId, Pageable pageable) {
        Page<ReportedUser> reportedUser = reportedUserRepository.findByProcessedFalseAndReportedUserId(reportedUserId, pageable);
        return reportedUser.map(ReportDetailDto::toDto);
    }

    @Override
    public Page<ReportedPostDto> searchAllNotProgressedPostReports(Pageable pageable) {
        return customReportRepository.findDistinctReportedPostByProcessed(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDetailDto> getAllNotProgressedReportsByPost(Long reportedObjectId, Pageable pageable) {
        Page<ReportedPost> reportedPost = reportedPostRepository.findByProcessedFalseAndPostId(reportedObjectId, pageable);
        return reportedPost.map(ReportDetailDto::toDto);
    }

    @Override
    @Transactional
    public ResultType handleReports(DType dType, Long reportedObjectId, ResultType resultType) {
        ResultType result;
        if (dType.equals(DType.USER)) {
            result = handleUserReports(reportedObjectId, resultType);
        } else {
            result = handlePostReports(reportedObjectId, resultType);
        }
        return result;
    }

    @Transactional
    public ResultType handleUserReports(Long reportedUserId, ResultType resultType) {
        if (resultType.equals(ResultType.FORBIDDEN)) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 타입은 사용자 신고 처리에 적합하지 않습니다.");
        }

        List<ReportedUser> report = reportedUserRepository.findByProcessedFalseAndReportedUserIdAndCreateDateLessThan(reportedUserId, LocalDateTime.now());
        if (report.isEmpty() || report.size() == 0) {
            throw new CustomException(ErrorCode.REPORT_NOT_FOUND_ERROR, "해당 사용자를 신고한 내역이 존재하지 않습니다.");
        }

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

    @Transactional
    public ResultType handlePostReports(Long postId, ResultType resultType) {
        if (!resultType.equals(ResultType.FORBIDDEN) && !resultType.equals(ResultType.NP)) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 타입은 게시글 신고 처리에 적합하지 않습니다.");
        }

        List<ReportedPost> reportedPostList = reportedPostRepository.findByProcessedFalseAndPostIdAndCreateDateLessThan(postId, LocalDateTime.now());
        if (reportedPostList.isEmpty() || reportedPostList.size() == 0) {
            throw new CustomException(ErrorCode.REPORT_NOT_FOUND_ERROR, "해당 게시글을 신고한 내역이 존재하지 않습니다.");
        }

        Post post = reportedPostList.get(0).getPost();
        for (ReportedPost reported : reportedPostList) {
            reported.reportProcessed();
            reported.updateResult(resultType);
        }

        if (resultType.equals(ResultType.FORBIDDEN)) {
            post.forbid();
            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(null, null, post.getUser(),
                    NotificationType.REPORTED_RESULT, messageSource.getMessage("notification.reported.result.forbidden", new Object[]{post.getTitle()}, null)));
        }

        return resultType;
    }
}
