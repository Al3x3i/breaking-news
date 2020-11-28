package com.breaking.news.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RssNewsLoader {

    public static List fetchTitlesFromXmlRss(String url) {
        List allTitles = new ArrayList();

        try {
            try (XmlReader reader = new XmlReader(new URL(url))) {
                SyndFeed feed = new SyndFeedInput().build(reader);
                System.out.println(feed.getTitle());
                for (SyndEntry entry : feed.getEntries()) {
                    allTitles.add(entry.getTitle());
                }
            }
        } catch (Exception ex) {
            log.error("Error occurred while fetching titles from url: `{}`. Error message", url, ex.getMessage());
        }
        return allTitles;
    }
}
