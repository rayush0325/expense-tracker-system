package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.HelloResponseDto;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class HelloController {
    @GetMapping("api/hello")
    public HelloResponseDto hello(){
        return new HelloResponseDto("hello from backend");
    }
}
