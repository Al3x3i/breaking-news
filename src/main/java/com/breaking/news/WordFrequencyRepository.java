package com.breaking.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface WordFrequencyRepository extends JpaRepository<WordFrequency, Long> {

}
