package com.breaking.news;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class TopNewsResponse extends RepresentationModel<TopNewsResponse> {

    List<TopNews> topNews = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class TopNews {
        private String word;
        private Integer counter;
        private List<RssItem> rssItems = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    public static class RssItem {
        String title;
        String link;
    }
}
