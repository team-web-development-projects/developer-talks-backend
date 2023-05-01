package com.dtalks.dtalks.question.entity;

import com.dtalks.dtalks.answer.entity.Answer;
import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.question.dto.QuestionDto;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "question", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Answer> answerList = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public static  Question toEntity(QuestionDto questionDto, User user) {
        return Question.builder()
                .user(user)
                .title(questionDto.getTitle())
                .content(questionDto.getContent())
                .build();
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }



}
