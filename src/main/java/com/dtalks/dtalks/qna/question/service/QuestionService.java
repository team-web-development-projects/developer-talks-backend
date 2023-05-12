package com.dtalks.dtalks.qna.question.service;

import com.dtalks.dtalks.qna.question.dto.QuestionDto;
import com.dtalks.dtalks.qna.question.dto.QuestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface QuestionService {
    QuestionResponseDto searchById(Long id);

    Page<QuestionResponseDto> searchAllQuestion(Pageable pageable);

    Page<QuestionResponseDto> searchQuestions(String keyword, Pageable pageable);

    Long createQuestion(QuestionDto questionDto);

    Long updateQuestion(Long id, QuestionDto questionDto);

    void deleteQuestion(Long id);

}
