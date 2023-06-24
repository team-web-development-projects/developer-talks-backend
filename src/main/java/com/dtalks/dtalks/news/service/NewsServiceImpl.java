package com.dtalks.dtalks.news.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.news.entity.News;
import com.dtalks.dtalks.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    @Override
    public List<News> getNews() {
        List<News> newsList = newsRepository.findTop20ByOrderByDateDesc();
        if (newsList.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "최신 뉴스가 존재하지 않습니다. ");
        }

        return newsList;
    }
}