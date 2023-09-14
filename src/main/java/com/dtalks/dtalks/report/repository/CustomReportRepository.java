package com.dtalks.dtalks.report.repository;

import com.dtalks.dtalks.admin.report.dto.ReportedPostDto;
import com.dtalks.dtalks.admin.report.dto.ReportedUserDto;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.entity.QPost;
import com.dtalks.dtalks.report.entity.QReport;
import com.dtalks.dtalks.user.entity.QUser;
import com.dtalks.dtalks.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.dtalks.dtalks.report.entity.QReportedPost.reportedPost;
import static com.dtalks.dtalks.report.entity.QReportedUser.reportedUser1;
@Repository
public class CustomReportRepository {
    private JPAQueryFactory jpaQueryFactory;

    public CustomReportRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<ReportedUserDto> findDistinctReportedUserByProcessed(Pageable pageable) {
        List<User> user = jpaQueryFactory.select(reportedUser1.reportedUser)
                .from(reportedUser1)
                .innerJoin(QReport.report).on(reportedUser1.id.eq(QReport.report.id).and(QReport.report.processed.eq(false)))
                .innerJoin(reportedUser1.reportedUser, QUser.user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        List<ReportedUserDto> content = user.stream().map(ReportedUserDto::toDto).toList();

        Long count = jpaQueryFactory.select(reportedUser1.reportedUser.countDistinct())
                .from(reportedUser1)
                .where(reportedUser1.processed.eq(false))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    public Page<ReportedPostDto> findDistinctReportedPostByProcessed(Pageable pageable) {
        List<Post> post = jpaQueryFactory.select(reportedPost.post).distinct()
                .from(reportedPost)
                .innerJoin(QReport.report).on(reportedPost.id.eq(QReport.report.id).and(QReport.report.processed.eq(false)))
                .innerJoin(reportedPost.post, QPost.post)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        List<ReportedPostDto> content = post.stream().map(ReportedPostDto::toDto).toList();

        Long count = jpaQueryFactory.select(reportedPost.post.countDistinct())
                .from(reportedPost)
                .where(reportedPost.processed.eq(false))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }
}
