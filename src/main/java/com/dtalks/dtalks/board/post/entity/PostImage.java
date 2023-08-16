package com.dtalks.dtalks.board.post.entity;

import com.dtalks.dtalks.base.entity.Document;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Post post;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    Document document;

    private Long orderNum;

    @Builder
    public PostImage(Post post, Document document, Long orderNum) {
        this.post = post;
        this.document = document;
        this.orderNum = orderNum;
    }

    public void updateOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }
}
