package com.breaking.news.analyzer;


import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(MockitoExtension.class)
public class OpenNLPAEnglishAnalyzerTest {

    @Test
    @SneakyThrows
    public void should_get_only_nouns_from_english_nlp_analyzer() {
        // GIVEN
        String message = "Ex-CIA Head John Brennan Calls Ted Cruz 'Simple-Minded' in Twitter Row Over Iran Killing - Newsweek";

        // WHEN
        List<String> result = OpenNLPAEnglishAnalyzer.getNounsFromText(message);

        // THEN
        then(String.join(" ", result)).isEqualTo("excia head john brennan call cruz simplemind twitter row kill newsweek");
    }
}
