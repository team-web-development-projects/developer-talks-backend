package com.dtalks.dtalks.admin.report.entity;

import com.dtalks.dtalks.admin.report.enums.ReportType;
import com.dtalks.dtalks.admin.report.enums.ResultType;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Report extends ReportTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User reportUser;

    @NotNull
    private ReportType reportType;

    private String detail;

    @NotNull
    private boolean processed;

    private ResultType resultType;
}
