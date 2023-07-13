package com.dtalks.dtalks.qna.question.service;

import com.dtalks.dtalks.board.post.dto.PutRequestDto;
import com.dtalks.dtalks.qna.question.dto.QuestionDto;
import com.dtalks.dtalks.qna.question.dto.QuestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    QuestionResponseDto searchById(Long id);
    Page<QuestionResponseDto> searchAllQuestion(Pageable pageable);
    Page<QuestionResponseDto> searchQuestionsByUser(String userId, Pageable pageable);
    Page<QuestionResponseDto> searchByKeyword(String keyword, Pageable pageable);
    List<QuestionResponseDto> search5BestQuestions();

    Long createQuestion(QuestionDto questionDto);

    Long updateQuestion(Long questionId, PutRequestDto putRequestDto);
    void deleteQuestion(Long id);

}
