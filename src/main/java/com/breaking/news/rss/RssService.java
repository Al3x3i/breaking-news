package com.breaking.news.rss;

import com.breaking.news.analyzer.OpenNLPAEnglishAnalyzer;
import com.breaking.news.model.Analysis;
import com.breaking.news.model.RssItem;
import com.breaking.news.model.WordFrequency;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RssService {

    @Autowired
    @Qualifier("rssAnalysisThreadPool")
    private ExecutorService executorService;

    public static List<RssResponse> loadRssResponses(List<String> urls) {
        return urls.stream().map(url -> RssNewsLoader.fetchTitlesFromXmlRss(url)).collect(Collectors.toList());
    }

    public Map<String, WordFrequency> analyseRssResponseItems(Analysis analysis, List<RssResponse> responseItems) {

        Map<String, WordFrequency> wordsPerTitle = new HashMap<>();
        List<Pair<RssItem, HashSet<String>>> titleWordsPerRssItem = new ArrayList<>();

        processRssResponsesAsynchronously(responseItems, titleWordsPerRssItem);

        for (Pair<RssItem, HashSet<String>> rssItemListPair : titleWordsPerRssItem) {
            addNewWordsPerRssItem(wordsPerTitle, rssItemListPair.getFirst(), rssItemListPair.getSecond(), analysis);
        }

        return wordsPerTitle;
    }

    private void processRssResponsesAsynchronously(List<RssResponse> responseItems, List<Pair<RssItem, HashSet<String>>> titleWordsPerRssItem) {
        Collection<Callable<List<Pair<RssItem, HashSet<String>>>>> callables = new ArrayList<>();

        responseItems.forEach(response -> callables.add(() -> getAllTitles(response)));

        try {
            List<Future<List<Pair<RssItem, HashSet<String>>>>> rssItemsFutures = executorService.invokeAll(callables);
            for (Future<List<Pair<RssItem, HashSet<String>>>> rssItems : rssItemsFutures) {
                titleWordsPerRssItem.addAll(rssItems.get());
            }
        } catch (Exception ex) {
            log.error("Error occurred while processing callable jobs. Error: `{}`", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static void addNewWordsPerRssItem(Map<String, WordFrequency> wordsPerTitle, RssItem rssResponseItem, Set<String> newWords, Analysis analysis) {

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

    private static List<Pair<RssItem, HashSet<String>>> getAllTitles(RssResponse item) {
        List<Pair<RssItem, HashSet<String>>> allWords = new ArrayList<>();
        for (RssItem rssResponseItem : item.getRssResponseItems()) {
            List words = OpenNLPAEnglishAnalyzer.getNounsFromText(rssResponseItem.getTile());

            Pair<RssItem, HashSet<String>> pair = Pair.of(rssResponseItem, getUniqueNounsPerTitle(words));
            allWords.add(pair);
        }
        return allWords;
    }

    private static HashSet<String> getUniqueNounsPerTitle(List<String> words) {
        return new HashSet<>(words);
    }
}
