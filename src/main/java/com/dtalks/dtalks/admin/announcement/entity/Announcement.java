package com.dtalks.dtalks.admin.announcement.entity;

import com.dtalks.dtalks.admin.announcement.dto.AnnounceDto;
import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    @NotNull
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull
    private String content;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount;

    public static Announcement toEntity(AnnounceDto announceDto, User user) {
        return Announcement.builder()
                .user(user)
                .title(announceDto.getTitle())
                .content(announceDto.getContent())
                .viewCount(0)
                .build();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateViewCount(){
        this.viewCount++;
    }
}
