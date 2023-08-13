package com.dtalks.dtalks.board.post.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    List<PostImage> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> commentList = new ArrayList<>();

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
    public static Post toEntity(PostRequestDto postDto, User user) {
        return Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .user(user)
                .viewCount(0)
                .favoriteCount(0)
                .recommendCount(0)
                .commentCount(0)
                .forbidden(false)
                .build();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void plusCommentCount() {
        this.commentCount++;
    }

    public void minusCommentCount() {
        this.commentCount--;
    }
}
