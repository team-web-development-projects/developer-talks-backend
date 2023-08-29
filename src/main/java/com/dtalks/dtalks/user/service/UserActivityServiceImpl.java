package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.board.comment.repository.CommentRepository;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.qna.answer.entity.Answer;
import com.dtalks.dtalks.qna.answer.repository.AnswerRepository;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.question.repository.QuestionRepository;
import com.dtalks.dtalks.studyroom.repository.StudyRoomUserRepository;
import com.dtalks.dtalks.user.dto.RecentActivityDto;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActivityType;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserActivityServiceImpl implements UserActivityService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final StudyRoomUserRepository studyRoomUserRepository;

    @Override
    @Transactional
    public Page<RecentActivityDto> getRecentActivities(UserDetails userDetails, String nickname, Pageable pageable) {
        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다."));
        if (user.getIsPrivate()) {
            User currentUser = (User) userDetails;
            if (userDetails == null || !currentUser.getNickname().equals(nickname)) {
                throw new CustomException(ErrorCode.ACCEPTED_BUT_IMPOSSIBLE, "비공개 설정으로 사용자의 최근활동 조회가 불가능합니다.");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime goe = now.minusDays(30);

        List<RecentActivityDto> activityList = new ArrayList<>();

        List<Post> postList = postRepository.findByForbiddenFalseAndUserIdAndCreateDateBetween(user.getId(), goe, now);
        for (Post post : postList) {
            activityList.add(RecentActivityDto.toDto(post.getId(), null, ActivityType.POST, post.getTitle(), user.getNickname(), post.getCreateDate()));
        }

        List<Comment> commentList = commentRepository.findByUserIdAndCreateDateBetween(user.getId(), goe, now);
        for (Comment comment : commentList) {
            Post post = comment.getPost();
            activityList.add(RecentActivityDto.toDto(post.getId(), comment.getId(), ActivityType.COMMENT, post.getTitle(), post.getUser().getNickname(), comment.getCreateDate()));
        }

        List<Question> questionList = questionRepository.findByUserIdAndCreateDateBetween(user.getId(), goe, now);
        for (Question question : questionList) {
            activityList.add(RecentActivityDto.toDto(question.getId(), null, ActivityType.QUESTION, question.getTitle(), user.getNickname(), question.getCreateDate()));
        }

        List<Answer> answerList = answerRepository.findByUserIdAndCreateDateBetween(user.getId(), goe, now);
        for (Answer answer : answerList) {
            Question question = answer.getQuestion();
            activityList.add(RecentActivityDto.toDto(question.getId(), answer.getId(), ActivityType.ANSWER, question.getTitle(), question.getUser().getNickname(), answer.getCreateDate()));
        }

        activityList.sort(Comparator.comparing(RecentActivityDto::getCreateDate).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), activityList.size());
        return new PageImpl<>(activityList.subList(start, end), pageable, activityList.size());
    }
}
