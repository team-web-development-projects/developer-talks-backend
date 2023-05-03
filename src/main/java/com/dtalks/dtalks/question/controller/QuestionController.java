package com.dtalks.dtalks.question.controller;

import com.dtalks.dtalks.question.dto.QuestionDto;
import com.dtalks.dtalks.question.dto.QuestionResponseDto;
import com.dtalks.dtalks.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.searchById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<QuestionResponseDto>> searchAll(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(questionService.searchAllQuestion(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<QuestionResponseDto>> searchQuestions(@RequestParam String keyword, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(questionService.searchQuestions(keyword, pageable));
    }

    @PostMapping
    public ResponseEntity<Long> createQuestion(@Valid @RequestBody QuestionDto questionDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(questionService.createQuestion(questionDto, userDetails));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateQuestion(@Valid @RequestBody QuestionDto questionDto, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(questionService.updateQuestion(id, questionDto, userDetails));
    }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        questionService.deleteQuestion(id, userDetails);
    }
}
