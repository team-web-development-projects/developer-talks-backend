package com.dtalks.dtalks.report.repository;

import com.dtalks.dtalks.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
