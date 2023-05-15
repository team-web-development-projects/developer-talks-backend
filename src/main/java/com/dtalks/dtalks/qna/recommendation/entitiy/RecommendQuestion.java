package com.dtalks.dtalks.qna.recommendation.entitiy;

import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
    @Builder
    public static RecommendQuestion toEntity(User user, Question question) {
        return RecommendQuestion.builder()
                .user(user)
                .question(question)
                .build();
    }

}
