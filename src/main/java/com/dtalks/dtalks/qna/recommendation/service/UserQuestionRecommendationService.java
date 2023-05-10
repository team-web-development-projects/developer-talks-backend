package com.dtalks.dtalks.qna.recommendation.service;

import com.dtalks.dtalks.qna.recommendation.dto.RecommendQuestionDto;

public interface UserQuestionRecommendationService {
    Long recommendQuestion(RecommendQuestionDto recommend);

    Long unRecommendQuestion(RecommendQuestionDto unRecommend);
}
