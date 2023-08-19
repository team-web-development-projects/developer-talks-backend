package com.dtalks.dtalks.report.entity;

import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("USER")
public class ReportedUser extends Report {
    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    private User reportedUser;
}
