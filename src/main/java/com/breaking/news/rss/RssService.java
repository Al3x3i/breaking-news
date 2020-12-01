package com.breaking.news.rss;

import com.breaking.news.analyzer.OpenNLPAEnglishAnalyzer;
import com.breaking.news.model.Analysis;
import com.breaking.news.model.RssItem;
import com.breaking.news.model.WordFrequency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class RssService {

    public Map<String, WordFrequency> analyseRssResponseItems(Analysis analysis, List<RssResponse> titles) {

        Map<String, WordFrequency> wordsPerTitle = new HashMap<>();
        for (RssResponse item : titles) {

            for (RssItem rssResponseItem : item.getRssResponseItems()) {
                HashSet<String> newUniqueWords = getUniqueNounsPerTitle(rssResponseItem);

                addNewWordsPerRssItem(wordsPerTitle, rssResponseItem, newUniqueWords, analysis);
            }
        }
        return wordsPerTitle;
    }

    private HashSet<String> getUniqueNounsPerTitle(RssItem rssResponseItem) {
        List<String> formattedTitleWords = OpenNLPAEnglishAnalyzer.getNounsFromText(rssResponseItem.getTile());
        return new HashSet<>(formattedTitleWords);
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
}
