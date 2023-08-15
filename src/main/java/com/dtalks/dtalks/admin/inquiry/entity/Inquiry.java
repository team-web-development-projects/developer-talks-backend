package com.dtalks.dtalks.admin.inquiry.entity;

import com.dtalks.dtalks.admin.inquiry.dto.InquiryDto;
import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry extends BaseTimeEntity {
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

    private Boolean isPrivate;

    public static Inquiry toEntity(InquiryDto inquiryDto, User user) {
        return Inquiry.builder()
                .user(user)
                .title(inquiryDto.getTitle())
                .content(inquiryDto.getContent())
                .viewCount(0)
                .isPrivate(inquiryDto.getIsPrivate())
                .build();
    }

    public void update(String title, String content, Boolean isPrivate) {
        this.title = title;
        this.content = content;
        this.isPrivate = isPrivate;
    }

    public void updateViewCount(){
        this.viewCount++;
    }
}
