package com.dtalks.dtalks.answer.service;

import com.dtalks.dtalks.answer.dto.AnswerDto;
import com.dtalks.dtalks.answer.dto.AnswerResponseDto;
import com.dtalks.dtalks.answer.entity.Answer;
import com.dtalks.dtalks.question.entity.Question;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AnswerService {

    List<AnswerResponseDto> getAnswerByQuestionId(Long questionId);

    Long createAnswer(AnswerDto answerDto, Long questionId, UserDetails userDetails);

    Long updateAnswer(Long answerId, AnswerDto answerDto, UserDetails userDetails);
    void deleteAnswer(Long id, UserDetails userDetails);

}
