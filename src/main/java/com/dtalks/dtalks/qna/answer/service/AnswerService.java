package com.dtalks.dtalks.qna.answer.service;

import com.dtalks.dtalks.qna.answer.dto.AnswerDto;
import com.dtalks.dtalks.qna.answer.dto.AnswerResponseDto;

import java.util.List;

public interface AnswerService {


    AnswerResponseDto searchById(Long id);
    List<AnswerResponseDto> getAnswersByQuestionId(Long questionId);
    List<AnswerResponseDto> getAnswersByUserId(Long userId);
    Long createAnswer(AnswerDto answerDto, Long questionId);

    Long updateAnswer(Long answerId, AnswerDto answerDto);
    void deleteAnswer(Long id);

    void selectAnswer(Long id);
    void cancelSelect(Long id);

}
