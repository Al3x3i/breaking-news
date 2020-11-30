package com.breaking.news.rss;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
    @Embeddable
    public static class RssResponseItem {
        private String tile;
        private String link;
    }
}
