package com.dtalks.dtalks.board.comment.repository;

import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.board.post.entity.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.dtalks.dtalks.board.comment.entity.QComment.comment;
@Repository
public class CustomCommentRepository {

    private JPAQueryFactory jpaQueryFactory;

    public CustomCommentRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<Comment> findAllByPost(Post post){
        return jpaQueryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .fetchJoin()
                .where(comment.post.id.eq(post.getId()))
                .orderBy(comment.parent.id.asc().nullsFirst(), comment.createDate.asc())
                .fetch();
    }
}
