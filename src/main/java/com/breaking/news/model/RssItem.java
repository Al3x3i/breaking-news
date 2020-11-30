package com.breaking.news.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RssItem {

    private String tile;

    @Column(columnDefinition = "VARCHAR(2048)")
    private String link;
}
