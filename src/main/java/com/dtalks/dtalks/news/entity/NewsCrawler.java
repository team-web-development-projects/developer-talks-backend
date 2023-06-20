package com.dtalks.dtalks.news.entity;

import com.dtalks.dtalks.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NewsCrawler {
    private static final int MAX_LENGTH = 100;
    private final NewsRepository newsRepository;
    @Scheduled(fixedDelay = 3600000) // 1시간마다 실행 (단위: 밀리초)
    @Transactional
    @Async
    public void crawlNews() throws IOException {
        List<News> newsList = new ArrayList<>();

        String pageUrl = "https://www.bloter.net/news/articleList.html?sc_sub_section_code=S2N15&view_type=sm";//크롤링할 사이트
        Document document = Jsoup.connect(pageUrl).get();
        Elements elements = document.select("#section-list > ul > li");// 뉴스 요소 css

        for (Element element : elements) {
            String title = element.select("h2.titles").text();
            String content = element.select("p.lead.line-6x2").text();
            String writer = element.select("em:nth-child(2)").text();
            String image = element.select("img").attr("src");
            String date = element.select("em:nth-child(3)").text();
            String url = "https://www.bloter.net/" + element.select("a.thumb").attr("href");

            //DB에 같은 뉴스 있으면 skip
            Optional<News> optionalNews = Optional.ofNullable(newsRepository.findByUrl(url));
            if (optionalNews.isPresent()) {
                continue;
            }
            //title, content 문자열 자르기
            if (title.length() > MAX_LENGTH) {
                title = title.substring(0, MAX_LENGTH);
            }
            if (content.length() > MAX_LENGTH) {
                content = content.substring(0, MAX_LENGTH);
            }

            News news = News.toEntity(title, content, writer, image, date, url);
            newsList.add(news);
        }
        newsRepository.saveAll(newsList);
    }
}