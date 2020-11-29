package com.breaking.news.rss;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class RssResponse {

    private String url;
    private List<RssResponseItem> rssResponseItems;

    public RssResponse(String url) {
        this.url = url;
        this.rssResponseItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RssResponseItem {
        private String tile;
        private String link;
    }
}
