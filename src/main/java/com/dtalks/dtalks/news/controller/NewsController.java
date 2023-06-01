package com.dtalks.dtalks.news.controller;

import com.dtalks.dtalks.news.entity.News;
import com.dtalks.dtalks.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/news")
    public ResponseEntity<List<News>> getNews() {
        List<News> newsList = newsService.getNews();
        return ResponseEntity.ok(newsList);
    }
}
