package com.breaking.news;

import com.breaking.news.model.WordFrequency;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface WordFrequencyRepository extends JpaRepository<WordFrequency, Long> {

    @Query(value = "SELECT w.* FROM WORD_FREQUENCY w WHERE analysis_id = :id ORDER BY w.counter DESC LIMIT :limit", nativeQuery = true)
    List<WordFrequency> getWordFrequenciesByAnalysisId(Long id, Integer limit);

}
