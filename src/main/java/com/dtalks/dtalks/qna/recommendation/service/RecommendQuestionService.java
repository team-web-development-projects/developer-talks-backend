package com.dtalks.dtalks.qna.recommendation.service;

public interface RecommendQuestionService {
    Integer recommendQuestion(Long questionId);

    Integer unRecommendQuestion(Long questionId);
}
