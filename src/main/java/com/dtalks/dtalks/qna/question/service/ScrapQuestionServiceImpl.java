package com.dtalks.dtalks.qna.question.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.question.entity.ScrapQuestion;
import com.dtalks.dtalks.qna.question.repository.QuestionRepository;
import com.dtalks.dtalks.qna.question.repository.ScrapQuestionRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScrapQuestionServiceImpl implements ScrapQuestionService {

    private final ScrapQuestionRepository scrapQuestionRepository;

    private final UserRepository userRepository;

    private final QuestionRepository questionRepository;


    @Override
    @Transactional
    public void addScrap(Long questionId) {
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
    }

    @Override
    @Transactional
    public void removeScrap(Long questionId) {
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
    }
}
