package com.breaking.news;

import com.breaking.news.TopNewsResponse.RssItem;
import com.breaking.news.TopNewsResponse.TopNews;
import com.breaking.news.model.WordFrequency;
import java.util.List;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
public class ApiController {

    @Autowired
    private BreakingNewsService breakingNewsService;

    @PostMapping(value = "/analyse/new", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity createNews(@RequestBody @Size(min = 2, max = 5) List<String> urls) {

        Long createdFeedId = breakingNewsService.createNewsRecord(urls);
        return ResponseEntity.ok(new CreateNewsFeedResponse(createdFeedId));
    }

    @GetMapping("/frequency/{id}")
    public ResponseEntity loadNewsById(@PathVariable long id) {
        List<WordFrequency> wordFrequencies = breakingNewsService.findHotNewsById(id);
        TopNewsResponse response = new TopNewsResponse();

        wordFrequencies.stream().forEach(wordFrequency -> {

            List<RssItem> rssItems = wordFrequency.getRssResponseItems()
                    .stream()
                    .map(item -> new RssItem(item.getTile(), item.getLink()))
                    .collect(toList());

            response.topNews.add(new TopNews(wordFrequency.getWord(), wordFrequency.getCounter(), rssItems));
        });

        response.add(linkTo(WebMvcLinkBuilder.methodOn(ApiController.class).loadNewsById(id))
                .withSelfRel());

        return ResponseEntity.ok(response);
    }

    @Data
    @AllArgsConstructor
    public static class CreateNewsFeedResponse {
        Long id;
    }
}
