package com.dtalks.dtalks.admin.report.repository;

import com.dtalks.dtalks.admin.report.entity.ReportedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportedUserRepository extends JpaRepository<ReportedUser, Long> {
    Page<ReportedUser> findByReportUserId(Long id, Pageable pageable);
}
