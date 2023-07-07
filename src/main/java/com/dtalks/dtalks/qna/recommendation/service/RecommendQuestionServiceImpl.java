package com.dtalks.dtalks.qna.recommendation.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.entity.Notification;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.repository.NotificationRepository;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.question.repository.QuestionRepository;
import com.dtalks.dtalks.qna.recommendation.entitiy.RecommendQuestion;
import com.dtalks.dtalks.qna.recommendation.repository.RecommendQuestionRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendQuestionServiceImpl implements RecommendQuestionService {
    private final RecommendQuestionRepository recommendQuestionRepository;
    private final QuestionRepository questionRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final MessageSource messageSource;

    @Override
    @Transactional
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
        recommendQuestionRepository.save(recommendQuestion);

        question.updateRecommendCount(true);

        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(recommendQuestion.getId(), question.getId(), question.getUser(),
                NotificationType.RECOMMEND_QUESTION, messageSource.getMessage("notification.question.recommend", new Object[]{question.getTitle()}, null)));
        return question.getRecommendCount();
    }

    @Override
    @Transactional
    public Integer unRecommendQuestion(Long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if(optionalQuestion.isEmpty()){
            throw new CustomException(ErrorCode.QUESTION_NOT_FOUND_ERROR, "해당 질문글이 존재하지 않습니다. ");
        }

        Question question = optionalQuestion.get();
        User user = SecurityUtil.getUser();

        if(!recommendQuestionRepository.existsByUserAndQuestion(user,question)){
            throw new CustomException(ErrorCode.RECOMMENDATION_NOT_FOUND_ERROR, "해당 질문글을 추천한 적이 없습니다 . ");
        }

        question.updateRecommendCount(false);
        RecommendQuestion recommendQuestion = recommendQuestionRepository.findByQuestionId(questionId);

        recommendQuestionRepository.deleteByUserAndQuestion(user, question);

        Notification notification = notificationRepository.findByRefIdAndType(recommendQuestion.getId(), NotificationType.RECOMMEND_QUESTION)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND_ERROR, "해당하는 알림이 존재하지 않습니다."));
        if (notification.getReadStatus().equals(ReadStatus.READ)) {
            notification.readDataDeleteSetting();
        } else {
            notificationRepository.deleteById(notification.getId());
        }

        return question.getRecommendCount();
    }

}
