package com.dtalks.dtalks.qna.answer.controller;

import com.dtalks.dtalks.qna.answer.dto.AnswerDto;
import com.dtalks.dtalks.qna.answer.dto.AnswerResponseDto;
import com.dtalks.dtalks.qna.answer.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/answers")
public class AnswerController {
    private final AnswerService answerService;

    @Operation(summary = "특정 답변 조회")
    @GetMapping("{id}")
    public ResponseEntity<AnswerResponseDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(answerService.searchById(id));
    }

    @Operation(summary = "특정 질문글의 답변들 조회")
    @GetMapping("/list/question/{questionId}")
    public ResponseEntity<List<AnswerResponseDto>> getAnswersByQuestionId(@PathVariable Long questionId) {
        return ResponseEntity.ok(answerService.getAnswersByQuestionId(questionId));
    }

    @Operation(summary = "특정 유저의 답변 리스트 조회")
    @GetMapping("list/user/{userId}")
    public ResponseEntity<List<AnswerResponseDto>> getAnswersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(answerService.getAnswersByUserId(userId));
    }

    @Operation(summary = "특정 질문글에 답변 등록")
    @PostMapping("/{questionId}")
    public ResponseEntity<Long> createAnswer(@Valid @RequestBody AnswerDto answerDto, @PathVariable Long questionId) {
        return ResponseEntity.ok(answerService.createAnswer(answerDto, questionId));
    }

    @Operation(summary = "답변 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAnswer(@Valid @RequestBody AnswerDto answerDto, @PathVariable Long id) {
        return ResponseEntity.ok(answerService.updateAnswer(id, answerDto));
    }

    @Operation(summary = "답변 삭제")
    @DeleteMapping("/{id}")
    public void deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
    }

    @Operation(summary = "답변 채택")
    @PostMapping("/{id}/select")
    public void selectAnswer(@PathVariable Long id) {
        answerService.selectAnswer(id);
    }
}
