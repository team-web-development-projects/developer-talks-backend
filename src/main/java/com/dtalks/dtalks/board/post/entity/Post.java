package com.dtalks.dtalks.board.post.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> commentList = new ArrayList<>();

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer favoriteCount;

    @Builder
    public static Post toEntity(PostRequestDto postDto, User user) {
        return Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .user(user)
                .viewCount(0)
                .favoriteCount(0)
                .build();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
