package com.breaking.news;

import com.breaking.news.model.Analysis;
import com.breaking.news.model.WordFrequency;
import com.breaking.news.rss.RssResponse;
import com.breaking.news.rss.RssService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private RssService rssService;

    @Transactional
    public Long createNewsRecord(List<String> urls) {
        log.info("Start fetching the RSS feeds from urls: `{}` ", urls);
        List<RssResponse> rssResponses = rssService.loadRssResponses(urls);

        Analysis analysis = Analysis.builder().rssRequest(urls).build();

        Map<String, WordFrequency> wordsPerTitle = rssService.analyseRssResponseItems(analysis, rssResponses);

        analysisRepository.saveAndFlush(analysis);
        wordFrequencyRepository.saveAll(new ArrayList<>(wordsPerTitle.values()));

        log.info("Finished fetching the RSS feeds from urls: `{}` ", urls);
        return analysis.getId();
    }

    public List<WordFrequency> findHotNewsById(Long id) {
        return wordFrequencyRepository.getWordFrequenciesByAnalysisId(id, MAX_TOP_NEWS);
    }
}
