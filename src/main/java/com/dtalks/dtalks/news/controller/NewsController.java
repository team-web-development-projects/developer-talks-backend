package com.dtalks.dtalks.news.controller;

import com.dtalks.dtalks.news.entity.News;
import com.dtalks.dtalks.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @Operation(summary = "News 클로링 해오는 api", description = "BLOTER 사이트의 IT 기업 new 기사(제목, 내용, 작성자, 날짜, 이미지 주소, 링크) 반환")
    @GetMapping("/news")
    public ResponseEntity<List<News>> getNews() {
        List<News> newsList = newsService.getNews();
        return ResponseEntity.ok(newsList);
    }
}