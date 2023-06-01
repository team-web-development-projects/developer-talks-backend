package com.dtalks.dtalks.news.entity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsCrawler {
    public List<News> crawlNews() throws IOException {
        List<News> newsList = new ArrayList<>();

        String url = "";//크롤링할 사이트
        Document document = Jsoup.connect("https://www.bloter.net/news/articleList.html?sc_sub_section_code=S2N15&view_type=sm").get();
        Elements element = document.select("#section-list > ul");// 뉴스 요소 css

        for (Element element1 : element) {
            String title = element1.select("#section-list > ul > li:nth-child(1) > div > h2").text();
            String content = element1.select("#section-list > ul > li:nth-child(1) > div > p").text();
            String writer = element1.select("#section-list > ul > li:nth-child(1) > div > span > em:nth-child(2)").text();
            String image = element1.select("#section-list > ul > li:nth-child(1) > a > img").attr("src");
            String date = element1.select("#section-list > ul > li:nth-child(1) > div > span > em:nth-child(3)").text();

            News news = new News(title, content, writer, date, image, url);
            newsList.add(news);
        }
        return newsList;
    }
}
