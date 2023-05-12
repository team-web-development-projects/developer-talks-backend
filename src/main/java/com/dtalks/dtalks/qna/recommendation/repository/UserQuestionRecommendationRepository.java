package com.dtalks.dtalks.qna.recommendation.repository;

import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.recommendation.entitiy.UserQuestionRecommendation;
import com.dtalks.dtalks.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestionRecommendationRepository extends JpaRepository<UserQuestionRecommendation,Long> {
    boolean existsByUserAndQuestion(User user, Question question);

    void deleteByUserAndQuestion(User user, Question question);
}
