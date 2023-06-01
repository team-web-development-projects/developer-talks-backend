package com.dtalks.dtalks.news.entity;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {
    private String title;

    private String content;

    private String writer;

    private String date;

    private String image;

    private String url;
}
