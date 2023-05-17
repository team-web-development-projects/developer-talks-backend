package com.dtalks.dtalks.qna.answer.entity;

import com.dtalks.dtalks.qna.answer.dto.AnswerDto;
import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean selected;

    @Builder
    public static Answer toEntity(AnswerDto answerDto, Question question, User user) {
        return Answer.builder()
                .user(user)
                .question(question)
                .content(answerDto.getContent())
                .selected(false)
                .build();
    }

    public void update(String content) {
        this.content = content;
    }

    public void setSelected(boolean selected) { this.selected = selected; }
}
