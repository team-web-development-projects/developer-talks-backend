package com.dtalks.dtalks.news.repository;

import com.dtalks.dtalks.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
    News findByUrl(String url);
}