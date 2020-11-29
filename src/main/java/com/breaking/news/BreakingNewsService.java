package com.breaking.news;

import com.breaking.news.analyzer.OpenNLPAEnglishAnalyzer;
import com.breaking.news.model.WordFrequency;
import com.breaking.news.rss.RssNewsLoader;
import com.breaking.news.rss.RssResponse;
import com.breaking.news.rss.RssResponse.RssResponseItem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BreakingNewsService {

    public void createNewsRecord(List<String> urls) {
        log.info("Fetch the RSS feeds from urls: `{}` ", urls);
        List<RssResponse> titles = urls.stream().map(url -> RssNewsLoader.fetchTitlesFromXmlRss(url)).collect(Collectors.toList());

        // analyze the RSS feed
        Map<String, WordFrequency> wordsPerTitle = new HashMap<>();

        for (RssResponse item : titles) {

            for (RssResponseItem rssResponseItem : item.getRssResponseItems()) {
                HashSet<String> newUniqueWords = getUniqueNounsPerTitle(rssResponseItem);

                addNewWordsPerRssItem(wordsPerTitle, rssResponseItem, newUniqueWords);
            }
        }
    }

    public void addNewWordsPerRssItem(Map<String, WordFrequency> wordsPerTitle, RssResponseItem rssResponseItem, Set<String> newWords) {

        for (String word : newWords) {

            if (wordsPerTitle.containsKey(word)) {
                WordFrequency wordsFrequency = wordsPerTitle.get(word);
                wordsFrequency.incrementCounter();
                wordsFrequency.getRssResponseItems().add(rssResponseItem);
            } else {
                wordsPerTitle.put(word, new WordFrequency(word, rssResponseItem));
            }
        }
    }

    private HashSet<String> getUniqueNounsPerTitle(RssResponseItem rssResponseItem) {
        List<String> formattedTitleWords = OpenNLPAEnglishAnalyzer.getInstance().getNounsFromText(rssResponseItem.getTile());
        return new HashSet<>(formattedTitleWords);
    }
}
