package com.dtalks.dtalks.board.post.repository;

import com.dtalks.dtalks.board.post.entity.Post;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.dtalks.dtalks.board.post.entity.QPost.post;
import static com.dtalks.dtalks.board.post.entity.QFavoritePost.favoritePost;

@Repository
public class CustomPostRepository {

    private JPAQueryFactory jpaQueryFactory;

    public CustomPostRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<Post> searchFavoritePost(Long userId, Pageable pageable) {
        List<Post> content = jpaQueryFactory.select(favoritePost.post)
                .from(favoritePost)
                .where(userIdEq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(postSort(pageable) )
                .fetch();

        Long count = jpaQueryFactory.select(post.count())
                .from(favoritePost)
                .where(userIdEq(userId))
                .fetchOne();
        return new PageImpl<>(content, pageable, count);
    }

    private OrderSpecifier<?> postSort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "id":
                        return new OrderSpecifier(direction, post.id);
                    case "title":
                        return new OrderSpecifier(direction, post.title);
                    case "viewCount":
                        return new OrderSpecifier(direction, post.viewCount);
                    case "recommendCount":
                        return new OrderSpecifier(direction, post.recommendCount);
                    case "favoriteCount":
                        return new OrderSpecifier(direction, post.favoriteCount);
                }
            }
        }
        return null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? favoritePost.user.id.eq(userId) : null;
    }

}
