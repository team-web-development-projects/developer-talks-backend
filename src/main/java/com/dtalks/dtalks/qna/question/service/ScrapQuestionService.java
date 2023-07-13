package com.dtalks.dtalks.qna.question.service;

import com.dtalks.dtalks.qna.question.dto.QuestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScrapQuestionService {

    Integer addScrap(Long questionId);

    Integer removeScrap(Long questionId);
    Page<QuestionResponseDto> searchScrapQuestionsByUser(String userId, Pageable pageable);

    boolean checkScrap(Long questionId);
}
