package com.dtalks.dtalks.qna.question.repository;

import com.dtalks.dtalks.qna.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String keyword1, String keyword2, Pageable pageable);

    Page<Question> findAllByOrderByIdDesc(Pageable pageable);

    Page<Question> findByUserId(Long id, Pageable pageable);

    List<Question> findTop5ByCreateDateGreaterThanEqualOrderByRecommendCountDesc(LocalDateTime startDateTime);

    List<Question> findByUserIdAndCreateDateBetween(Long userId, LocalDateTime goe, LocalDateTime loe);
}
