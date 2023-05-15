package com.dtalks.dtalks.qna.recommendation.controller;

import com.dtalks.dtalks.qna.recommendation.dto.RecommendQuestionDto;
import com.dtalks.dtalks.qna.recommendation.service.RecommendQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("recommendations")
public class RecommendQuestionController {
    private final RecommendQuestionService recommendQuestionService;

    @PostMapping("/question/recommend")
    public ResponseEntity<Long> recommendQuestion(@Valid @RequestBody RecommendQuestionDto recommendQuestionDto){
        return ResponseEntity.ok(recommendQuestionService.recommendQuestion(recommendQuestionDto));
    }

    @DeleteMapping("/question/unrecommend")
    public ResponseEntity<Long> unrecommendQuestion(@Valid @RequestBody RecommendQuestionDto unRecommendQuestionDto){
        return ResponseEntity.ok(recommendQuestionService.unRecommendQuestion(unRecommendQuestionDto));
    }
}

