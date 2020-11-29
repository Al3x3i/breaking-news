package com.breaking.news;

import com.breaking.news.model.WordFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface WordFrequencyRepository extends JpaRepository<WordFrequency, Long> {

    public Object[] findByAnalysisId(Long id);

}
