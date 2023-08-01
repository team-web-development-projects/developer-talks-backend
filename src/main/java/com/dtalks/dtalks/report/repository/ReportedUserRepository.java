package com.dtalks.dtalks.report.repository;

import com.dtalks.dtalks.report.entity.ReportedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportedUserRepository extends JpaRepository<ReportedUser, Long> {
    Long countByReportedUserIdAndProcessed(Long userId, boolean processed);
    boolean existsByDtypeAndReportUserIdAndReportedUserIdAndProcessed(String dtype, Long reportUserId, Long reportedUserId, boolean processed);
}
