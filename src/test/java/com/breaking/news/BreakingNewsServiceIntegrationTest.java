package com.breaking.news;

import com.breaking.news.BreakingNewsService.WordsFrequency;
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
    public void should_add_new_word_and_increase_counter_for_existing_words() {

        // GIVEN
        Map<String, WordsFrequency> wordsPerTitle = new HashMap<>();
        wordsPerTitle.put("test_1", new WordsFrequency("test_1", RssResponseItem.builder().build()));
        wordsPerTitle.put("test_2", new WordsFrequency("test_2", RssResponseItem.builder().build()));
        wordsPerTitle.put("test_3", new WordsFrequency("test_3", RssResponseItem.builder().build()));

        // WHEN

        breakingNewsService.addNewWordsPerRssItem(wordsPerTitle, RssResponseItem.builder().build(), Sets.newSet("test_1", "test_4"));

        // THEN
        then(wordsPerTitle.size()).isEqualTo(4);
        then(wordsPerTitle.get("test_1").getCounter()).isEqualTo(2);
    }
}
