package com.dtalks.dtalks.question.service;

import com.dtalks.dtalks.question.dto.QuestionDto;
import com.dtalks.dtalks.question.dto.QuestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface QuestionService {
    QuestionResponseDto searchById(Long id);

    Page<QuestionResponseDto> searchAllQuestion(Pageable pageable);

    Page<QuestionResponseDto> searchQuestions(String keyword, Pageable pageable);

    Long createQuestion(QuestionDto questionDto, UserDetails user);

    Long updateQuestion(Long id, QuestionDto questionDto, UserDetails user);

    void deleteQuestion(Long id, UserDetails user);

}
