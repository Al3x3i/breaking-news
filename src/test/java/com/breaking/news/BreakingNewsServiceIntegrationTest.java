package com.breaking.news;

import com.breaking.news.model.WordFrequency;
import com.breaking.news.rss.RssResponse.RssResponseItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
public class BreakingNewsServiceIntegrationTest {

    @Autowired
    private BreakingNewsService breakingNewsService;

    Map<String, WordFrequency> wordsPerTitle = new HashMap<>();

    @Test
    public void should_create_feed_record() {

        // GIVEN
        List urls = List.of("https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en");

        // WHEN
        breakingNewsService.createNewsRecord(urls);

        // THEN
        // TODO
    }

    @Test
    public void should_add_new_words_and_increase_counter_for_existing_words() {

        // GIVEN
        givenWordsPerTitle(List.of("test_1", "test_2", "test_3"));

        // WHEN
        breakingNewsService.addNewWordsPerRssItem(wordsPerTitle, RssResponseItem.builder().build(), Sets.newSet("test_1", "test_4"));

        // THEN
        then(wordsPerTitle.size()).isEqualTo(4);
        then(wordsPerTitle.get("test_1").getCounter()).isEqualTo(2);
    }

    @Test
    public void should_find_rss_news_by_most_popular_words() {

        // GIVEN

        // WHEN

        // THEN

    }

    private void givenWordsPerTitle(List<String> newWords) {
        newWords.stream().forEach(word -> wordsPerTitle.put(word, new WordFrequency(word, RssResponseItem.builder().build())));
    }

}
