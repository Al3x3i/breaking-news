package com.breaking.news.rss;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
public class RssNewsLoaderIntegrationTest {

    @Test
    public void should_fetch_news_titles_from_url() {
        // GIVEN
        String url = "https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en";

        // WHEN
        List allTitles = RssNewsLoader.fetchTitlesFromXmlRss(url);

        // THEN
        then(allTitles.size()).isNotZero();
    }
}
