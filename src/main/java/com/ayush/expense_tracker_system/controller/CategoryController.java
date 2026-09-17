package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    @GetMapping("/")
    public ResponseEntity<List<Category>> getCategoryList(){
        String[] categoryNameArr = {"food", "travel", "study"};

        List<Category> categoryList = Arrays.stream(categoryNameArr)
                .map(categoryName -> new Category(categoryName))
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryList);
    }
}
