package com.breaking.news;

import com.breaking.news.model.Analysis;
import com.breaking.news.model.WordFrequency;
import com.breaking.news.rss.RssResponse.RssResponseItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class ApiRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BreakingNewsService breakingNewsService;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions resultActions;

    private Long persistedAnalysis;

    @AfterEach
    public void tearDown() {
        analysisRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    public void should_create_news() {
        // GIVEN
        String requestBody = objectMapper.writeValueAsString(List.of(
                "https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en",
                "https://www.espn.com/espn/rss/news"));

        // WHEN
        resultActions = mockMvc.perform(post("/analyse/new")
                .contentType(APPLICATION_JSON)
                .content(requestBody));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("id").isNotEmpty());


        String id = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), "id").toString();
        then(analysisRepository.findAll()).isNotNull();
        then(analysisRepository.findAll().size()).isEqualTo(1);
        then(breakingNewsService.findHotNewsById(Long.valueOf(id))).isNotNull();
    }

    @Test
    @SneakyThrows
    public void should_find_news_by_id() {
        // GIVEN
        givenPersistedAnalyses();

        // WHEN
        resultActions = mockMvc.perform(get("/frequency/{id}", persistedAnalysis));

        // THEN
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("topNews[*]", hasSize(3)))
                .andExpect(jsonPath("topNews[*].word").isNotEmpty())
                .andExpect(jsonPath("topNews[*].counter").isNotEmpty())
                .andExpect(jsonPath("topNews[*].rssItems").isNotEmpty())
                .andExpect(jsonPath("topNews[*].rssItems[*].title").isNotEmpty())
                .andExpect(jsonPath("topNews[*].rssItems[*].link").isNotEmpty())

                // Validate Order
                .andExpect(jsonPath("topNews[0].word", is("third")))
                .andExpect(jsonPath("topNews[1].word", is("second")))
                .andExpect(jsonPath("topNews[2].word", is("fourth")));
    }

    private void givenPersistedAnalyses() {
        Analysis analysis = new Analysis();
        analysis.setRssRequest(List.of("google.rss"));

        var wordFrequencyFirst = new WordFrequency("first", new RssResponseItem("news", "www"), analysis);

        var wordFrequencySecond = new WordFrequency("second", new RssResponseItem("news", "www"), analysis);
        IntStream.range(1, 5).forEach(index -> wordFrequencySecond.incrementCounter());

        var wordFrequencyThird = new WordFrequency("third", new RssResponseItem("news", "www"), analysis);
        IntStream.range(1, 10).forEach(index -> wordFrequencyThird.incrementCounter());

        var wordFrequencyFourth = new WordFrequency("fourth", new RssResponseItem("news", "www"), analysis);
        IntStream.range(1, 3).forEach(index -> wordFrequencyFourth.incrementCounter());

        analysis.setWordFrequencies(List.of(wordFrequencyFirst, wordFrequencySecond, wordFrequencyThird, wordFrequencyFourth));

        persistedAnalysis = analysisRepository.saveAndFlush(analysis).getId();
    }

}
