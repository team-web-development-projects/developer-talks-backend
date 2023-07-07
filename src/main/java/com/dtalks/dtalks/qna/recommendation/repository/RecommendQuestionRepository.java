package com.dtalks.dtalks.qna.recommendation.repository;

import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.recommendation.entitiy.RecommendQuestion;
import com.dtalks.dtalks.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendQuestionRepository extends JpaRepository<RecommendQuestion,Long> {
    boolean existsByUserAndQuestion(User user, Question question);

    void deleteByUserAndQuestion(User user, Question question);

    RecommendQuestion findByQuestionId(Long questionId);
}
