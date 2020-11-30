package com.breaking.news.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    @ElementCollection(targetClass = RssItem.class)
    @CollectionTable(name = "RSS_RESPONSE_ITEM", joinColumns = @JoinColumn(name = "WORD_FREQUENCY_ID"))
    private List<RssItem> rssResponseItems;

    public WordFrequency(String word, RssItem rssResponseItem, Analysis analysis) {
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
