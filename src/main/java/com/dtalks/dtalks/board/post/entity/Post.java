package com.dtalks.dtalks.board.post.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    @NotNull
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull
    private String content;

    private String thumbnailUrl;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    List<PostImage> imageList;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer commentCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer favoriteCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer recommendCount;

    private boolean forbidden;

    @Builder
    public Post(PostRequestDto postDto, User user) {
        this.title = postDto.getTitle();
        this.content = postDto.getContent();
        this.user = user;
        this.commentCount = 0;
        this.viewCount = 0;
        this.favoriteCount = 0;
        this.recommendCount = 0;
        this.forbidden = false;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateViewCount() {
        this.viewCount++;
    }

    public void updateThumbnail(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void plusCommentCount() {
        this.commentCount++;
    }

    public void minusCommentCount() {
        this.commentCount--;
    }

    public void plusRecommentCount() {
        this.recommendCount++;
    }

    public void minusRecommentCount() {
        this.recommendCount--;
    }

    public void plusFavoriteCount() {
        this.favoriteCount++;
    }

    public void minusFavoriteCount() {
        this.favoriteCount--;
    }

    public void forbiddenSetting() {
        this.forbidden = true;
    }

    public void restoreSetting() {
        this.forbidden = false;
    }
}
