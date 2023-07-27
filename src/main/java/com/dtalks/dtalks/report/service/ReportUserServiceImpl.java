package com.dtalks.dtalks.report.service;

import com.dtalks.dtalks.report.dto.ReportDetailDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportUserServiceImpl implements ReportUserService {

    private final UserRepository userRepository;
    private final ReportedUserRepository reportedUserRepository;

    @Override
    public Page<ReportDetailDto> searchAllUserReports(Pageable pageable) {
        User user = SecurityUtil.getUser();
        Page<ReportedUser> page = reportedUserRepository.findByReportUserId(user.getId(), pageable);
        return page.map(ReportDetailDto::toDto);
    }

    @Override
    public void report(String nickname, ReportDetailRequestDto dto) {
        User reportUser = SecurityUtil.getUser();
        User reportedUser = userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "해당하는 사용자를 찾을 수 없습니다."));

        ReportedUser report = ReportedUser.builder()
                .reportUser(reportUser)
                .reportType(dto.getReportType())
                .detail(dto.getDetail())
                .processed(false)
                .resultType(ResultType.WAIT)
                .reportedUser(reportedUser)
                .build();
        reportedUserRepository.save(report);
    }

    @Override
    public void cancelReport(Long id) {
        ReportedUser report = reportedUserRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND_ERROR, "신고 내역이 없습니다."));

        if (report.isProcessed()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "이미 처리된 신고 사항입니다.");
        }

        reportedUserRepository.delete(report);
    }
}
