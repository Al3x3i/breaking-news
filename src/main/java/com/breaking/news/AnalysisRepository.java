package com.breaking.news;


import com.breaking.news.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}
