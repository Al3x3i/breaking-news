package com.breaking.news.rss;

import com.breaking.news.model.RssItem;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class RssResponse {

    private final String url;
    private final List<RssItem> rssResponseItems;

    public RssResponse(String url) {
        this.url = url;
        this.rssResponseItems = new ArrayList<>();
    }
}
