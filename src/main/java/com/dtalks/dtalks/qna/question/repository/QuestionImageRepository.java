package com.dtalks.dtalks.qna.question.repository;

import com.dtalks.dtalks.qna.question.entity.QuestionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionImageRepository extends JpaRepository<QuestionImage, Long> {
    List<QuestionImage> findByQuestionId(Long questionId);

    List<QuestionImage> findByQuestionIdOrderByOrderNum(Long questionId);

    Optional<QuestionImage> findTop1ByQuestionId(Long questionId);
}
