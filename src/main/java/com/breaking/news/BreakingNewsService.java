package com.breaking.news;

import com.breaking.news.analyzer.OpenNLPAEnglishAnalyzer;
import com.breaking.news.model.Analysis;
import com.breaking.news.model.RssItem;
import com.breaking.news.model.WordFrequency;
import com.breaking.news.rss.RssNewsLoader;
import com.breaking.news.rss.RssResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BreakingNewsService {

    private final static Integer MAX_TOP_NEWS = 3;
    @Autowired
    private AnalysisRepository analysisRepository;
    @Autowired
    private WordFrequencyRepository wordFrequencyRepository;

    @Transactional
    public Long createNewsRecord(List<String> urls) {
        log.info("Fetch the RSS feeds from urls: `{}` ", urls);
        List<RssResponse> titles = urls.stream().map(url -> RssNewsLoader.fetchTitlesFromXmlRss(url)).collect(Collectors.toList());

        Analysis analysis = Analysis.builder().rssRequest(urls).build();

        Map<String, WordFrequency> wordsPerTitle = new HashMap<>();

        for (RssResponse item : titles) {

            for (RssItem rssResponseItem : item.getRssResponseItems()) {
                HashSet<String> newUniqueWords = getUniqueNounsPerTitle(rssResponseItem);

                addNewWordsPerRssItem(wordsPerTitle, rssResponseItem, newUniqueWords, analysis);
            }
        }

        analysisRepository.saveAndFlush(analysis);
        wordFrequencyRepository.saveAll(wordsPerTitle.values().stream().collect(Collectors.toList()));
        return analysis.getId();
    }

    public void addNewWordsPerRssItem(Map<String, WordFrequency> wordsPerTitle, RssItem rssResponseItem, Set<String> newWords, Analysis analysis) {

        for (String word : newWords) {

            if (wordsPerTitle.containsKey(word)) {
                WordFrequency wordsFrequency = wordsPerTitle.get(word);
                wordsFrequency.incrementCounter();
                wordsFrequency.getRssResponseItems().add(rssResponseItem);
            } else {
                wordsPerTitle.put(word, new WordFrequency(word, rssResponseItem, analysis));
            }
        }
    }

    public List<WordFrequency> findHotNewsById(Long id) {
        return wordFrequencyRepository.getWordFrequenciesByAnalysisId(id, MAX_TOP_NEWS);
    }

    private HashSet<String> getUniqueNounsPerTitle(RssItem rssResponseItem) {
        List<String> formattedTitleWords = OpenNLPAEnglishAnalyzer.getInstance().getNounsFromText(rssResponseItem.getTile());
        return new HashSet<>(formattedTitleWords);
    }
}
