package com.shj.testapi.controller;

import com.shj.testapi.dto.TestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test")
    public TestResponseDto signup() {
        TestResponseDto testResponseDto = new TestResponseDto("Success - Test Success!");
        return testResponseDto;
    }
}
