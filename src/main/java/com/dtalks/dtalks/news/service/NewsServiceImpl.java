package com.dtalks.dtalks.news.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.news.entity.News;
import com.dtalks.dtalks.news.entity.NewsCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsCrawler newsCrawler;


    @Override
    public List<News> getNews() {
        try {
            List<News> newsList = newsCrawler.crawlNews();
            return newsList;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "뉴스가 존재하지 않습니다. ");

        }
    }
}
