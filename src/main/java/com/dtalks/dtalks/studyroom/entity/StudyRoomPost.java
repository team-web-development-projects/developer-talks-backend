package com.dtalks.dtalks.studyroom.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.studyroom.dto.PostRequestDto;
import com.dtalks.dtalks.studyroom.enums.Category;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class StudyRoomPost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("0")
    private Integer viewCount;

    @Column(nullable = false)
    private Category category;

    @ManyToOne
    private StudyRoom studyRoom;

    @ManyToOne
    private User user;

    public void addViewCount() {
        this.viewCount++;
    }
    public static StudyRoomPost toEntity(PostRequestDto postRequestDto, User user, StudyRoom studyRoom) {
        return StudyRoomPost.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .viewCount(0)
                .category(postRequestDto.getCategory())
                .studyRoom(studyRoom)
                .user(user)
                .build();

    }
}
