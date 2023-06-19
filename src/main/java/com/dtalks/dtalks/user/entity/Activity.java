package com.dtalks.dtalks.user.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.qna.answer.entity.Answer;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.user.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @OneToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private Answer answer;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @Builder
    public static Activity createQA(User user, Question question, Answer answer, ActivityType type) {
        return Activity.builder()
                .user(user)
                .question(question)
                .answer(answer)
                .type(type)
                .build();
    }

    @Builder
    public static Activity createBoard(User user, Post post, Comment comment, ActivityType type) {
        return Activity.builder()
                .user(user)
                .post(post)
                .comment(comment)
                .type(type)
                .build();
    }
}
