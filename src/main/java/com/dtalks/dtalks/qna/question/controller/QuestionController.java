package com.dtalks.dtalks.qna.question.controller;

import com.dtalks.dtalks.qna.question.service.QuestionService;
import com.dtalks.dtalks.qna.question.dto.QuestionDto;
import com.dtalks.dtalks.qna.question.dto.QuestionResponseDto;
import com.dtalks.dtalks.qna.question.service.ScrapQuestionService;
import com.dtalks.dtalks.qna.recommendation.dto.RecommendQuestionDto;
import com.dtalks.dtalks.qna.recommendation.service.RecommendQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    private final RecommendQuestionService recommendQuestionService;

    private final ScrapQuestionService scrapQuestionService;

    @Operation(summary = "특정 질문글 id로 조회")
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.searchById(id));
    }

    @Operation(summary = "모든 질문글 조회")
    @GetMapping("/all")
    public ResponseEntity<Page<QuestionResponseDto>> searchAll(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(questionService.searchAllQuestion(pageable));
    }

    @Operation(summary = "특정 유저의 질문글 조회")
    @GetMapping("/list/user/{userId}")
    public ResponseEntity<Page<QuestionResponseDto>> searchQuestionsByUser(@PathVariable String userId,
                                                                           @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(questionService.searchQuestionsByUser(userId, pageable));
    }

    @Operation(summary = "키워드로 질물글 조회(제목과 본문에 keyword 포함시 조회)")
    @GetMapping("/search")

    public ResponseEntity<Page<QuestionResponseDto>> searchQuestions(@RequestParam String keyword, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(questionService.searchQuestions(keyword, pageable));
    }

    @Operation(summary = "질문글 작성")
    @PostMapping
    public ResponseEntity<Long> createQuestion(@Valid @RequestBody QuestionDto questionDto) {
        return ResponseEntity.ok(questionService.createQuestion(questionDto));
    }

    @Operation(summary = "질문글 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateQuestion(@Valid @RequestBody QuestionDto questionDto, @PathVariable Long id) {
        return ResponseEntity.ok(questionService.updateQuestion(id, questionDto));
    }

    @Operation(summary = "질문글 삭제")
    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
    }

    @Operation(summary = "질문글 추천")
    @PostMapping("/recommend")
    public ResponseEntity<Long> recommendQuestion(@Valid @RequestBody RecommendQuestionDto recommendQuestionDto) {
        return ResponseEntity.ok(recommendQuestionService.recommendQuestion(recommendQuestionDto));
    }

    @Operation(summary = "질문글 추천 취소")
    @DeleteMapping("/recommend")
    public ResponseEntity<Long> unrecommendQuestion(@Valid @RequestBody RecommendQuestionDto unRecommendQuestionDto) {
        return ResponseEntity.ok(recommendQuestionService.unRecommendQuestion(unRecommendQuestionDto));
    }

    @Operation(summary = "질문글 스크랩")
    @PostMapping("/scrap/{id}")
    public void addScrap(@PathVariable Long id) {
        scrapQuestionService.addScrap(id);
    }

    @Operation(summary = "질문글 스크랩 취소")
    @DeleteMapping("/scrap/{id}")
    public void removeScrap(@PathVariable Long id) {
        scrapQuestionService.removeScrap(id);
    }
}
