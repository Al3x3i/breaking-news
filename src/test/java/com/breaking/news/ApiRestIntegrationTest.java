package com.breaking.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ApiRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions resultActions;

    @Test
    @SneakyThrows
    public void should_create_news() {
        // GIVEN
        String requestBody = objectMapper.writeValueAsString(List.of("first", "second"));

        // WHEN
        resultActions = mockMvc.perform(post("/analyse/new")
                .contentType(APPLICATION_JSON)
                .content(requestBody));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    public void should_find_news_by_id() {
        // GIVEN // WHEN
        resultActions = mockMvc.perform(get("/frequency/{id}", 1));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

}
