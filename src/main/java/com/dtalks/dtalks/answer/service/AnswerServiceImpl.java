package com.dtalks.dtalks.answer.service;

import com.dtalks.dtalks.answer.dto.AnswerDto;
import com.dtalks.dtalks.answer.dto.AnswerResponseDto;
import com.dtalks.dtalks.answer.entity.Answer;
import com.dtalks.dtalks.answer.repository.AnswerRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.PermissionNotGrantedException;
import com.dtalks.dtalks.exception.exception.PostNotFoundException;
import com.dtalks.dtalks.exception.exception.UserNotFoundException;
import com.dtalks.dtalks.question.entity.Question;
import com.dtalks.dtalks.question.repository.QuestionRepository;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponseDto> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId).stream()
                .map(AnswerResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createAnswer(AnswerDto answerDto,  Long questionId, UserDetails userDetails) {
        Optional<User> user = Optional.ofNullable(userRepository.getByUserid(userDetails.getUsername()));
        if (user.isEmpty()) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND_ERROR, "해당하는 유저가 존재하지 않습니다.");
        }
        Optional<Question> question = questionRepository.findById(questionId);
        if(question.isEmpty()){
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문이 존재하지 않습니다. ");
        }
        Answer answer = Answer.toEntity(answerDto, question.get(), user.get());
        answerRepository.save(answer);
        return answer.getId();
    }

    @Override
    @Transactional
    public Long updateAnswer(Long answerId, AnswerDto answerDto, UserDetails userDetails) {
        User user = userRepository.getByNickname(userDetails.getUsername());
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        if (optionalAnswer.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 답변이 존재하지 않습니다. ");
        }
        Answer answer = optionalAnswer.get();
        String userId = answer.getUser().getUserid();
        if (!userId.equals(userDetails.getUsername())) {
            throw new PermissionNotGrantedException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 답변을 수정할 수 있는 권한이 없습니다.");
        }
        answer.update(answerDto.getContent());
        return answerId;
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id, UserDetails userDetails) {
        Optional<Answer> optionalAnswer = answerRepository.findById(id);
        if(optionalAnswer.isEmpty()){
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 답변이 존재하지 않습니다. ");
        }
        Answer answer = optionalAnswer.get();
        String userId = answer.getUser().getUserid();
        if (!userId.equals(userDetails.getUsername())) {
            throw new PermissionNotGrantedException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 답변을 삭제할 수 있는 권한이 없습니다.");
        }
        answerRepository.delete(answer);
    }
}
