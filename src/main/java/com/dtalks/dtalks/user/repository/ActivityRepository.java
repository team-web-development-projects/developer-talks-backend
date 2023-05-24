package com.dtalks.dtalks.user.repository;

import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByPostId(Long postId);
    Optional<Activity> findByCommentId(Long comment);
    List<Activity> findByQuestionId(Long questionId);
    List<Activity> findByAnswerId(Long answerId);

    Optional<Activity> findByAnswerIdAndType(Long answerId, ActivityType type);
    Optional<Activity> findByQuestionIdAndType(Long questionId, ActivityType type);

    List<Activity> findByPostIdAndType(Long postId, ActivityType type);

    Page<Activity> findByUserIdAndCreateDateBetween(Long userId, LocalDateTime goe, LocalDateTime loe, Pageable pageable);
}
