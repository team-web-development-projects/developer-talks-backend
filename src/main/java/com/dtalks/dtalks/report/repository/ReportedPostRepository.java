package com.dtalks.dtalks.report.repository;

import com.dtalks.dtalks.report.entity.ReportedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportedPostRepository extends JpaRepository<ReportedPost, Long> {
    boolean existsByProcessedFalseAndDtypeAndReportUserIdAndPostId(String dtype, Long reportUserId, Long reportedUserId);
    Page<ReportedPost> findByProcessedFalseAndPostId(Long postId, Pageable pageable);

    List<ReportedPost> findByProcessedFalseAndPostIdAndCreateDateLessThan(Long postId, LocalDateTime createDate);
    List<ReportedPost> findByProcessedFalseAndPostId(Long postId);
}
