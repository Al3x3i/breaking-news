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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class RssService {

    @Autowired
    @Qualifier("rssAnalysisThreadPool")
    private ExecutorService executorService;

    public static List<RssResponse> loadRssResponses(List<String> urls) {
        return urls.stream().map(RssNewsLoader::fetchTitlesFromXmlRss).collect(toList());
    }

    public Map<String, WordFrequency> analyseRssResponseItems(Analysis analysis, List<RssResponse> responseItems) {

        Map<String, WordFrequency> wordsPerTitle = new HashMap<>();
        List<Pair<RssItem, HashSet<String>>> titleWordsPerRssItem = new ArrayList<>();

        processRssResponsesAsynchronously(responseItems, titleWordsPerRssItem);

        for (Pair<RssItem, HashSet<String>> rssItemListPair : titleWordsPerRssItem) {
            addAndRankNewWordsPerRssItem(wordsPerTitle, rssItemListPair.getFirst(), rssItemListPair.getSecond(), analysis);
        }

        return wordsPerTitle;
    }

    public static void addAndRankNewWordsPerRssItem(Map<String, WordFrequency> wordsPerTitle, RssItem rssResponseItem, Set<String> newWords, Analysis analysis) {

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

    private void processRssResponsesAsynchronously(List<RssResponse> responseItems, List<Pair<RssItem, HashSet<String>>> titleWordsPerRssItem) {
        Collection<Callable<List<Pair<RssItem, HashSet<String>>>>> callables = new ArrayList<>();

        responseItems.forEach(response -> callables.add(() -> getRssItemWords(response)));

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

    private static List<Pair<RssItem, HashSet<String>>> getRssItemWords(RssResponse item) {
        List<Pair<RssItem, HashSet<String>>> rssItemWords = new ArrayList<>();
        for (RssItem rssResponseItem : item.getRssResponseItems()) {
            var words = OpenNLPAEnglishAnalyzer.getNounsFromText(rssResponseItem.getTile());

            Pair pair = Pair.of(rssResponseItem, getUniqueWords(words));
            rssItemWords.add(pair);
        }
        return rssItemWords;
    }

    private static HashSet<String> getUniqueWords(List<String> words) {
        return new HashSet<>(words);
    }
}
