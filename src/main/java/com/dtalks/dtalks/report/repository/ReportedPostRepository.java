package com.dtalks.dtalks.report.repository;

import com.dtalks.dtalks.report.entity.ReportedPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportedPostRepository extends JpaRepository<ReportedPost, Long> {
    boolean existsByProcessedFalseAndDtypeAndReportUserIdAndPostId(String dtype, Long reportUserId, Long reportedUserId);

}
