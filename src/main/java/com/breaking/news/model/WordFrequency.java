package com.breaking.news.model;

import com.breaking.news.rss.RssResponse.RssResponseItem;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class WordFrequency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String word;

    @NotNull
    private Integer counter;

    @ManyToOne(optional = false)
    @Setter
    private Analysis analysis;

    @NotNull
    @Transient
    private List<RssResponseItem> rssResponseItems;

    public WordFrequency(String word, RssResponseItem rssResponseItem, Analysis analysis) {
        this.word = word;
        this.counter = 1;
        this.rssResponseItems = new ArrayList<>();
        this.rssResponseItems.add(rssResponseItem);
        this.analysis = analysis;
    }

    public void incrementCounter() {
        this.counter++;
    }
}
