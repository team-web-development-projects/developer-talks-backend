package com.dtalks.dtalks.news.repository;

import com.dtalks.dtalks.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    News findByUrl(String url);
    List<News> findTop20ByOrderByDateDesc();

}