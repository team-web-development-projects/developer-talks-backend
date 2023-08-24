package com.dtalks.dtalks.report.entity;

import com.dtalks.dtalks.board.post.entity.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@DiscriminatorValue("POST")
public class ReportedPost extends Report {
    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    private Post post;
}
