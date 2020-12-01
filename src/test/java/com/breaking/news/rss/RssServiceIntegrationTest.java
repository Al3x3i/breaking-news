package com.breaking.news.rss;

import com.breaking.news.model.Analysis;
import com.breaking.news.model.RssItem;
import com.breaking.news.model.WordFrequency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
public class RssServiceIntegrationTest {

    @Autowired
    private RssService rssService;

    private Map<String, WordFrequency> wordsPerTitle = new HashMap<>();

    @Test
    public void should_add_new_words_and_increase_counter_for_existing_words() {

        // GIVEN
        givenWordsPerTitle(List.of("test_1", "test_2", "test_3"));

        // WHEN
        rssService.addNewWordsPerRssItem(wordsPerTitle, RssItem.builder().build(), Sets.newSet("test_1", "test_4"), null);

        // THEN
        then(wordsPerTitle.size()).isEqualTo(4);
        then(wordsPerTitle.get("test_1").getCounter()).isEqualTo(2);
    }

    private void givenWordsPerTitle(List<String> newWords) {
        newWords.stream().forEach(word -> wordsPerTitle.put(word, new WordFrequency(word, new RssItem(), new Analysis())));
    }
}
