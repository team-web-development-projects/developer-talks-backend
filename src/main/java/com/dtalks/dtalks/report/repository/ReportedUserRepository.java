package com.dtalks.dtalks.report.repository;

import com.dtalks.dtalks.report.entity.ReportedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportedUserRepository extends JpaRepository<ReportedUser, Long> {
    boolean existsByDtypeAndReportUserIdAndReportedUserIdAndProcessed(String dtype, Long reportUserId, Long reportedUserId, boolean processed);
    Page<ReportedUser> findByReportedUserIdAndProcessed(Long reportedUserId, boolean processed, Pageable pageable);
    List<ReportedUser> findByReportedUserIdAndProcessedAndCreateDateLessThan(Long reportedUserId, boolean processed, LocalDateTime createDate);
    List<ReportedUser> findByReportedUserId(Long reportedUserId);
}
