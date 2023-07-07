package com.dtalks.dtalks.board.post.entity;

import com.dtalks.dtalks.base.entity.Document;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Post post;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    Document document;

    private Long orderNum;
}
