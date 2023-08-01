package com.dtalks.dtalks.report.repository;

import com.dtalks.dtalks.admin.report.dto.ReportedUserDto;
import com.dtalks.dtalks.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.dtalks.dtalks.report.entity.QReportedUser.reportedUser1;

@Repository
public class CustomReportedUserRepository {
    private JPAQueryFactory jpaQueryFactory;

    public CustomReportedUserRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<ReportedUserDto> findDistinctByProcessed(Pageable pageable) {
        List<User> user = jpaQueryFactory.select(reportedUser1.reportedUser)
                .from(reportedUser1)
                .where(jpaQueryFactory.selectFrom(reportedUser1)
                        .where(reportedUser1.processed.eq(false)).exists())
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
}
