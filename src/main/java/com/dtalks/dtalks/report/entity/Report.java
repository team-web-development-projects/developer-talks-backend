package com.dtalks.dtalks.report.entity;

import com.dtalks.dtalks.report.enums.ReportType;
import com.dtalks.dtalks.report.enums.ResultType;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class Report extends ReportTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable = false, updatable = false)
    private String dtype;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    private User reportUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    private String detail;

    @NotNull
    private boolean processed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultType resultType;

    public void reportProcessed() {
        this.processed = true;
    }

    public void updateResult(ResultType type) {
        this.resultType = type;
    }
}
