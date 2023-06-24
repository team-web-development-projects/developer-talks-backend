package com.dtalks.dtalks.qna.question.repository;

import com.dtalks.dtalks.qna.question.entity.QuestionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionImageRepository extends JpaRepository<QuestionImage, Long> {
    List<QuestionImage> findByQuestionId(Long questionId);
}
