package com.dtalks.dtalks.qna.recommendation.service;

public interface RecommendQuestionService {
    void recommendQuestion(Long questionId);

    void unRecommendQuestion(Long questionId);
}
