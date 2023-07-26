package com.dtalks.dtalks.admin.report.repository;

import com.dtalks.dtalks.admin.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
