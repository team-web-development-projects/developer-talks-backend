package com.dtalks.dtalks.qna.question.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.question.repository.QuestionRepository;
import com.dtalks.dtalks.qna.question.dto.QuestionDto;
import com.dtalks.dtalks.qna.question.dto.QuestionResponseDto;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActivityType;
import com.dtalks.dtalks.user.repository.ActivityRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ActivityRepository activityRepository;

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto searchById(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        Question question = optionalQuestion.get();
        question.updateViewCount();
        return QuestionResponseDto.toDto(question);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchAllQuestion(Pageable pageable) {
        Page<Question> questionPage = questionRepository.findAllByOrderByIdDesc(pageable);
        return questionPage.map(QuestionResponseDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchQuestionsByUser(String userId, Pageable pageable) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(userId));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }
        User user = optionalUser.get();
        Page<Question> questions = questionRepository.findByUserId(user.getId(), pageable);
        return questions.map(QuestionResponseDto::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchQuestions(String keyword, Pageable pageable) {
        Page<Question> questionPage = questionRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        if (questionPage.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        return questionPage.map(QuestionResponseDto::toDto);
    }

    @Override
    @Transactional
    public List<QuestionResponseDto> search5BestQuestions() {
        List<Question> top5Questions = questionRepository.findTop5ByOrderByLikeCountDesc();
        return top5Questions.stream().map(QuestionResponseDto::toDto).toList();
    }

    @Override
    @Transactional
    public Long createQuestion(QuestionDto questionDto) {
        User user = SecurityUtil.getUser();
        Question question = Question.toEntity(questionDto, user);
        questionRepository.save(question);

        Activity activity = Activity.builder()
                .question(question)
                .type(ActivityType.QUESTION)
                .user(user)
                .build();

        activityRepository.save(activity);

        return question.getId();
    }

    @Override
    @Transactional
    public Long updateQuestion(Long questionId, QuestionDto questionDto) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        Question question = optionalQuestion.get();
        String userId = question.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 질문글을 수정할 수 있는 권한이 없습니다. ");
        }
        question.update(questionDto.getTitle(), questionDto.getContent());
        return questionId;
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        Question question = optionalQuestion.get();
        if (!question.getAnswerList().isEmpty()) {
            throw new CustomException(ErrorCode.DELETE_NOT_PERMITTED_ERROR, "답변이 달린 질문은 삭제할 수 없습니다. ");
        }
        String userId = question.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 질문글을 삭제할 수 있는 권한이 없습니다. ");
        }

        // 삭제된 답변의 활동 기록은 남아있고 그 기록에 question이 연결되어있음
        List<Activity> activityList = activityRepository.findByQuestionId(id);
        for (Activity activity : activityList) {
            activity.setQuestion(null);
        }

        questionRepository.delete(question);
    }

}
