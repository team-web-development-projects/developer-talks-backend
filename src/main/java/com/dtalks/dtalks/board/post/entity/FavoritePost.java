package com.dtalks.dtalks.board.post.entity;

import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public static FavoritePost toEntity(Post post, User user) {
        return FavoritePost.builder()
                .post(post)
                .user(user)
                .build();
    }
}
