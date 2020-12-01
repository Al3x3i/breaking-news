package com.breaking.news.rss;

import com.breaking.news.model.RssItem;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RssNewsLoader {

    static RssResponse fetchTitlesFromXmlRss(String url) {
        RssResponse response = new RssResponse(url);
        try {
            try (XmlReader reader = new XmlReader(new URL(url))) {
                SyndFeed feed = new SyndFeedInput().build(reader);
                for (SyndEntry entry : feed.getEntries()) {
                    response.getRssResponseItems().add(
                            RssItem.builder()
                                    .tile(entry.getTitle())
                                    .link(entry.getLink())
                                    .build());
                }
            }
        } catch (Exception ex) {
            log.error("Error occurred while fetching titles from url: `{}`. Error message", url, ex.getMessage());
            throw new RuntimeException(ex);
        }
        return response;
    }
}
