package com.dtalks.dtalks.qna.recommendation.controller;

import com.dtalks.dtalks.qna.recommendation.dto.RecommendQuestionDto;
import com.dtalks.dtalks.qna.recommendation.service.UserQuestionRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("recommendations")
public class UserQuestionRecommendationController {
    private final UserQuestionRecommendationService userQuestionRecommendationService;

    @PostMapping("/question/recommend")
    public ResponseEntity<Long> recommendQuestion(@Valid @RequestBody RecommendQuestionDto recommendQuestionDto){
        return ResponseEntity.ok(userQuestionRecommendationService.recommendQuestion(recommendQuestionDto));
    }

    @DeleteMapping("/question/unrecommend")
    public ResponseEntity<Long> unrecommendQuestion(@Valid @RequestBody RecommendQuestionDto unRecommendQuestionDto){
        return ResponseEntity.ok(userQuestionRecommendationService.unRecommendQuestion(unRecommendQuestionDto));
    }
}

