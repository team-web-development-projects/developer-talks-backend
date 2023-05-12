package com.dtalks.dtalks.board.post.repository;

import com.dtalks.dtalks.board.post.entity.Post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import static com.dtalks.dtalks.board.post.entity.QPost.post;

@Repository
public class CustomPostRepository {

    private JPAQueryFactory jpaQueryFactory;

    public CustomPostRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public void updateFavoriteCount(Post requestPost, boolean plus) {
        if (plus) {
            jpaQueryFactory.update(post)
                    .set(post.favoriteCount, post.favoriteCount.add(1))
                    .where(post.eq(requestPost))
                    .execute();
        } else {
            jpaQueryFactory.update(post)
                    .set(post.favoriteCount, post.favoriteCount.subtract(1))
                    .where(post.eq(requestPost))
                    .execute();
        }
    }

    public void updateRecommendCount(Post requestPost, boolean plus) {
        if (plus) {
            jpaQueryFactory.update(post)
                    .set(post.recommendCount, post.recommendCount.add(1))
                    .where(post.eq(requestPost))
                    .execute();
        } else {
            jpaQueryFactory.update(post)
                    .set(post.recommendCount, post.recommendCount.subtract(1))
                    .where(post.eq(requestPost))
                    .execute();
        }
    }
}
