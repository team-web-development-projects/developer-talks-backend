package com.dtalks.dtalks.board.comment.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.board.comment.dto.CommentRequestDto;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotNull
    private String content;

    private boolean secret;

    private boolean removed;

    // 부모 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> childList;

    @Builder
    public Comment(CommentRequestDto commentRequestDto, Post post, User user) {
        this.content = commentRequestDto.getContent();
        this.secret = commentRequestDto.isSecret();
        this.post = post;
        this.user = user;
        this.removed = false;
    }

    @Builder(builderMethodName = "recommentBuilder", buildMethodName = "recommentBuild")
    public Comment(CommentRequestDto commentRequestDto, Post post, User user, Comment parent) {
        this.content = commentRequestDto.getContent();
        this.secret = commentRequestDto.isSecret();
        this.post = post;
        this.user = user;
        this.parent = parent;
        this.removed = false;
    }

    public void updateComment(String content, boolean secret) {
        this.content = content;
        this.secret = secret;
    }

    public void updateRemove() {
        this.removed = true;
    }
}
