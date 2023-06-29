package com.dtalks.dtalks.qna.recommendation.service;

import com.dtalks.dtalks.alarm.entity.Alarm;
import com.dtalks.dtalks.alarm.enums.AlarmType;
import com.dtalks.dtalks.alarm.repository.AlarmRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.question.repository.QuestionRepository;
import com.dtalks.dtalks.qna.recommendation.entitiy.RecommendQuestion;
import com.dtalks.dtalks.qna.recommendation.repository.RecommendQuestionRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendQuestionServiceImpl implements RecommendQuestionService {
    private final RecommendQuestionRepository recommendQuestionRepository;
    private final QuestionRepository questionRepository;
    private final AlarmRepository alarmRepository;

    @Override
    public Integer recommendQuestion(Long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if(optionalQuestion.isEmpty()){
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }

        Question question = optionalQuestion.get();
        User user = SecurityUtil.getUser();

        if(recommendQuestionRepository.existsByUserAndQuestion(user,question)){
            throw new CustomException(ErrorCode.RECOMMENDATION_ALREADY_EXIST_ERROR, "이미 해당 질문글을 추천하였습니다. ");
        }

        RecommendQuestion recommendQuestion = RecommendQuestion.toEntity(user, question);

        question.updateRecommendCount(true);
        recommendQuestionRepository.save(recommendQuestion);
        alarmRepository.save(Alarm.createAlarm(question.getUser(), AlarmType.RECOMMEND_QUESTION, "작성한 질문이 추천받았습니다.", "/questions/" + questionId));

        return question.getRecommendCount();
    }

    @Override
    @Transactional
    public Integer unRecommendQuestion(Long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if(optionalQuestion.isEmpty()){
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }

        Question question = optionalQuestion.get();
        User user = SecurityUtil.getUser();

        if(!recommendQuestionRepository.existsByUserAndQuestion(user,question)){
            throw new CustomException(ErrorCode.RECOMMENDATION_NOT_FOUND_ERROR, "이 질문글을 추천한 적이 없습니다 . ");
        }

        question.updateRecommendCount(false);
        recommendQuestionRepository.deleteByUserAndQuestion(user, question);
        return question.getRecommendCount();
    }

}
