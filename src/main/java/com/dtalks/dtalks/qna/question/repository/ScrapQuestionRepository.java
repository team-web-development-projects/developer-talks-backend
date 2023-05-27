package com.dtalks.dtalks.qna.question.repository;

import com.dtalks.dtalks.qna.question.entity.ScrapQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapQuestionRepository extends JpaRepository<ScrapQuestion, Long> {


    Optional<ScrapQuestion> findByQuestionIdAndUserId(Long questionId, Long UserId);
}
