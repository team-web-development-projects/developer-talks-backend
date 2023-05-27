package com.dtalks.dtalks.qna.question.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.qna.question.dto.QuestionResponseDto;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.question.entity.ScrapQuestion;
import com.dtalks.dtalks.qna.question.repository.QuestionRepository;
import com.dtalks.dtalks.qna.question.repository.ScrapQuestionRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScrapQuestionServiceImpl implements ScrapQuestionService {

    private final ScrapQuestionRepository scrapQuestionRepository;

    private final UserRepository userRepository;

    private final QuestionRepository questionRepository;


    @Override
    @Transactional
    public Integer addScrap(Long questionId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 질문글입니다.");
        }

        User user = optionalUser.get();
        Question question = optionalQuestion.get();

        if (user == question.getUser()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "작성한 글에는 즐겨찾기가 불가능합니다.");
        }
        Optional<ScrapQuestion> optionalScrapQuestion = scrapQuestionRepository.findByQuestionIdAndUserId(question.getId(), user.getId());
        if (optionalScrapQuestion.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_ERROR, "이미 스크랩 되어 있습니다.");
        }
        ScrapQuestion scrapQuestion = ScrapQuestion.toEntity(question, user);
        scrapQuestionRepository.save(scrapQuestion);

        question.updateScrap(true);
        return question.getScrapCount();
    }

    @Override
    @Transactional
    public Integer removeScrap(Long questionId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 질문글입니다.");
        }

        User user = optionalUser.get();
        Question question = optionalQuestion.get();

        Optional<ScrapQuestion> optionalScrapQuestion = scrapQuestionRepository.findByQuestionIdAndUserId(question.getId(), user.getId());
        if (optionalScrapQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_ERROR, "해당 질문글이 스크랩 되어있지 않습니다.");
        }

        ScrapQuestion scrapQuestion = optionalScrapQuestion.get();
        scrapQuestionRepository.delete(scrapQuestion);

        question.updateScrap(false);
        return question.getScrapCount();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchScrapQuestionsByUser(String userId, Pageable pageable) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }
        Page<ScrapQuestion> scrapQuestions = scrapQuestionRepository.findByUserId(userId, pageable);
        List<QuestionResponseDto> questionResponseDtos = scrapQuestions.getContent().stream()
                .map(scrapQuestion -> QuestionResponseDto.toDto(scrapQuestion.getQuestion()))
                .collect(Collectors.toList());

        return new PageImpl<>(questionResponseDtos, pageable, scrapQuestions.getTotalElements());
    }
}
