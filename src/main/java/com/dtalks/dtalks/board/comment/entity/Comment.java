package com.dtalks.dtalks.board.comment.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private boolean isSecret;

    private boolean isRemoved;

    // 부모 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> childList = new ArrayList<>();
}
