package com.breaking.news;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BreakingNewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BreakingNewsApplication.class, args);
    }

    @Bean("rssAnalysisThreadPool")
    public ExecutorService rssAnalysisThreadPool() {
        return Executors.newFixedThreadPool(10);
    }
}
