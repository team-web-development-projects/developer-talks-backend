package com.dtalks.dtalks.qna.question.entity;

import com.dtalks.dtalks.qna.answer.entity.Answer;
import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.qna.question.dto.QuestionDto;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<QuestionImage> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScrapQuestion> scrapQuestionList = new ArrayList<>();

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount;
    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer recommendCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer favoriteCount;

    @Builder
    public static  Question toEntity(QuestionDto questionDto, User user) {
        return Question.builder()
                .user(user)
                .title(questionDto.getTitle())
                .content(questionDto.getContent())
                .viewCount(0)
                .recommendCount(0)
                .favoriteCount(0)
                .build();
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void updateViewCount(){
        this.viewCount++;
    }

    public void updateRecommendCount(boolean like){
        if(like){
            this.recommendCount++;
        }
        else{
            this.recommendCount--;
        }
    }

    public void updateFavoriteCount(boolean scrap) {
        if (scrap) {
            this.favoriteCount++;
        } else {
            this.favoriteCount--;
        }
    }
}
