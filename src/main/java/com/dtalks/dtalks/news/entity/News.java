package com.dtalks.dtalks.news.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class News {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String writer;

    private String image;

    private String date;

    @Column(unique = true)
    private String url;


    @Builder
    public static News toEntity(String title, String content, String writer, String image, String date, String url) {
        return News.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .image(image)
                .date(date)
                .url(url)
                .build();
    }
}