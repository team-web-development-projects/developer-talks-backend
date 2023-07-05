package com.dtalks.dtalks.qna.answer.repository;

import com.dtalks.dtalks.qna.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long id);
    List<Answer> findByUserIdAndCreateDateBetween(Long userId, LocalDateTime goe, LocalDateTime loe);
    List<Answer> findByUserId(Long id);
}
