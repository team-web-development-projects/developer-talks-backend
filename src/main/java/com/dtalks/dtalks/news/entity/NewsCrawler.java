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

            News news = new News(title, content, writer, date, image, url);
            newsList.add(news);
        }
        return newsList;
    }
}
