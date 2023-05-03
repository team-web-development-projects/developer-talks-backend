package com.dtalks.dtalks.question.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.DeleteNotPermittedException;
import com.dtalks.dtalks.exception.exception.PermissionNotGrantedException;
import com.dtalks.dtalks.exception.exception.PostNotFoundException;
import com.dtalks.dtalks.exception.exception.UserNotFoundException;
import com.dtalks.dtalks.question.dto.QuestionDto;
import com.dtalks.dtalks.question.dto.QuestionResponseDto;
import com.dtalks.dtalks.question.entity.Question;
import com.dtalks.dtalks.question.repository.QuestionRepository;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto searchById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        return QuestionResponseDto.toDto(question.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchAllQuestion(Pageable pageable) {
        Page<Question> questionPage = questionRepository.findAllByOrderByIdDesc(pageable);
        return questionPage.map(QuestionResponseDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchQuestions(String keyword, Pageable pageable) {
        Page<Question> questionPage = questionRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        if (questionPage.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        return questionPage.map(QuestionResponseDto::toDto);
    }

    @Override
    @Transactional
    public Long createQuestion(QuestionDto questionDto, UserDetails userDetails) {
        Optional<User> user = Optional.ofNullable(userRepository.getByUserid(userDetails.getUsername()));
        if (user.isEmpty()) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND_ERROR, "해당하는 유저가 존재하지 않습니다. ");
        }
        Question question = Question.toEntity(questionDto, user.get());
        questionRepository.save(question);
        return question.getId();
    }

    @Override
    @Transactional
    public Long updateQuestion(Long questionId, QuestionDto questionDto, UserDetails userDetails) {
        User user = userRepository.getByNickname(userDetails.getUsername());
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        Question question = optionalQuestion.get();
        String userId = question.getUser().getUserid();
        if (!userId.equals(userDetails.getUsername())) {
            throw new PermissionNotGrantedException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 질문글을 수정할 수 있는 권한이 없습니다. ");
        }
        question.update(questionDto.getTitle(), questionDto.getContent());
        return questionId;
    }

    @Override
    public void deleteQuestion(Long id, UserDetails userDetails) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        Question question = optionalQuestion.get();
        if (!question.getAnswerList().isEmpty()) {
            throw new DeleteNotPermittedException(ErrorCode.DELETE_NOT_PERMITTED_ERROR, "답변이 달린 질문은 삭제할 수 없습니다. ");
        }
        String userId = question.getUser().getUserid();
        if (!userId.equals(userDetails.getUsername())) {
            throw new PermissionNotGrantedException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 질문글을 삭제할 수 있는 권한이 없습니다. ");
        }
        questionRepository.delete(question);
    }

}
