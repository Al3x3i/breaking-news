package com.breaking.news;

import com.breaking.news.rss.RssNewsLoader;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BreakingNewsService {

    public void createNewsRecord(List<String> urls) {
        log.info("Load news from urls: `{}` ", urls);
        urls.stream().peek(url -> RssNewsLoader.fetchTitlesFromXmlRss(url));
    }
}
