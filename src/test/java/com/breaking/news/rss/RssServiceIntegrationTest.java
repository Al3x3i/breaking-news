package com.breaking.news.rss;

import com.breaking.news.model.Analysis;
import com.breaking.news.model.RssItem;
import com.breaking.news.model.WordFrequency;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
public class RssServiceIntegrationTest {

    @Autowired
    private RssService rssService;

    private Map<String, WordFrequency> wordsPerTitle = new HashMap<>();

    @Test
    public void should_load_rss_item_and_count_word_frequency_in_right_order() {

        // GIVEN
        String samplePath = getClass().getClassLoader().getResource("sample.xml").toExternalForm();
        RssResponse rssResponse = RssNewsLoader.fetchTitlesFromXmlRss(samplePath);

        // WHEN
        Map<String, WordFrequency> wordsFrequency = rssService.analyseRssResponseItems(new Analysis(), List.of(rssResponse));

        // THEN
        thenVerifyWordsOrder(wordsFrequency);
    }

    @Test
    public void should_add_new_words_and_increase_counter_for_existing_words() {

        // GIVEN
        givenWordsPerTitle(List.of("test_1", "test_2", "test_3"));

        // WHEN
        RssService.addAndRankNewWordsPerRssItem(wordsPerTitle, RssItem.builder().build(), Sets.newSet("test_1", "test_4"), null);

        // THEN
        then(wordsPerTitle.size()).isEqualTo(4);
        then(wordsPerTitle.get("test_1").getCounter()).isEqualTo(2);
    }

    private void givenWordsPerTitle(List<String> newWords) {
        newWords.stream().forEach(word -> wordsPerTitle.put(word, new WordFrequency(word, new RssItem(), new Analysis())));
    }

    private void thenVerifyWordsOrder(Map<String, WordFrequency> wordsFrequency) {
        Map<String, WordFrequency> orderedWords = getOrderedWordsByCounter(wordsFrequency);

        Iterator<Entry<String, WordFrequency>> it = orderedWords.entrySet().iterator();

        Entry<String, WordFrequency> firstPair = it.next();
        then(firstPair.getKey()).isEqualTo("trump");
        then(firstPair.getValue().getWord()).isEqualTo("trump");
        then(firstPair.getValue().getCounter()).isEqualTo(4);
        then(firstPair.getValue().getRssResponseItems().size()).isEqualTo(4);

        Entry<String, WordFrequency> secondPair = it.next();
        then(secondPair.getKey()).isEqualTo("state");
        then(secondPair.getValue().getWord()).isEqualTo("state");
        then(secondPair.getValue().getCounter()).isEqualTo(4);
        then(secondPair.getValue().getRssResponseItems().size()).isEqualTo(4);

        Entry<String, WordFrequency> thirdPair = it.next();
        then(thirdPair.getKey()).isEqualTo("fox");
        then(thirdPair.getValue().getWord()).isEqualTo("fox");
        then(thirdPair.getValue().getCounter()).isEqualTo(4);
        then(thirdPair.getValue().getRssResponseItems().size()).isEqualTo(4);
    }

    private Map<String, WordFrequency> getOrderedWordsByCounter(Map<String, WordFrequency> wordsFrequency) {

        Map<String, WordFrequency> result = wordsFrequency.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(Comparator.comparing(WordFrequency::getCounter))))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }
}
