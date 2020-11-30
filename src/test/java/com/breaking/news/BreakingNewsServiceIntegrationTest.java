package com.breaking.news;

import com.breaking.news.model.Analysis;
import com.breaking.news.model.WordFrequency;
import com.breaking.news.rss.RssResponse.RssResponseItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BreakingNewsServiceIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private BreakingNewsService breakingNewsService;

    private Map<String, WordFrequency> wordsPerTitle = new HashMap<>();

    private Long persistedAnalysisId;

    @Test
    @Transactional
    public void should_create_feed_record() {

        // GIVEN
        List urls = List.of("https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en");

        // WHEN
        breakingNewsService.createNewsRecord(urls);

        // THEN
        then(analysisRepository.findAll()).isNotNull();
        then(analysisRepository.findAll().size()).isEqualTo(1);
        then(analysisRepository.findAll().get(0).getRssRequest().get(0)).isEqualTo(urls.get(0));
    }

    @Test
    public void should_add_new_words_and_increase_counter_for_existing_words() {

        // GIVEN
        givenWordsPerTitle(List.of("test_1", "test_2", "test_3"));

        // WHEN
        breakingNewsService.addNewWordsPerRssItem(wordsPerTitle, RssResponseItem.builder().build(), Sets.newSet("test_1", "test_4"), null);

        // THEN
        then(wordsPerTitle.size()).isEqualTo(4);
        then(wordsPerTitle.get("test_1").getCounter()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void should_find_rss_news_by_most_popular_words() {

        // GIVEN
        givenPersistedAnalyses();

        // WHEN
        List<WordFrequency> wordFrequencies = breakingNewsService.findHotNewsById(persistedAnalysisId);

        // THEN
        then(wordFrequencies).isNotNull();
        then(wordFrequencies.size()).isEqualTo(3);
        then(wordFrequencies.get(0).getCounter()).isEqualTo(10);
        then(wordFrequencies.get(1).getCounter()).isEqualTo(5);
        then(wordFrequencies.get(2).getCounter()).isEqualTo(3);
    }

    private void givenWordsPerTitle(List<String> newWords) {
        newWords.stream().forEach(word -> wordsPerTitle.put(word, new WordFrequency(word, new RssResponseItem(), new Analysis())));
    }

    private void givenPersistedAnalyses() {
        Analysis analysis = new Analysis();
        analysis.setRssRequest(List.of("google.rss"));

        var wordFrequencyFirst = new WordFrequency("first", new RssResponseItem(), analysis);

        var wordFrequencySecond = new WordFrequency("second", new RssResponseItem(), analysis);
        IntStream.range(1, 5).forEach(index -> wordFrequencySecond.incrementCounter());

        var wordFrequencyThird = new WordFrequency("third", new RssResponseItem(), analysis);
        IntStream.range(1, 10).forEach(index -> wordFrequencyThird.incrementCounter());

        var wordFrequencyFourth = new WordFrequency("fourth", new RssResponseItem(), analysis);
        IntStream.range(1, 3).forEach(index -> wordFrequencyFourth.incrementCounter());

        analysis.setWordFrequencies(List.of(wordFrequencyFirst, wordFrequencySecond, wordFrequencyThird, wordFrequencyFourth));

        analysisRepository.saveAndFlush(analysis);
        persistedAnalysisId = analysis.getId();
    }

}
