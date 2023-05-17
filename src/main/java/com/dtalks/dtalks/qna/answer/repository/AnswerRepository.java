package com.dtalks.dtalks.qna.answer.repository;

import com.dtalks.dtalks.qna.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long id);

    List<Answer> findByUserId(Long id);
}
