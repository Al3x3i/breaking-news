package com.breaking.news;

import java.util.List;
import javax.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
public class ApiController {

    @PostMapping(value = "/analyse/new", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity createNews(@RequestBody @Size(min = 2, max = 5) List<String> urls) {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/frequency/{id}")
    public ResponseEntity loadNewsById(@PathVariable long id) {
        return ResponseEntity.ok().build();
    }
}
