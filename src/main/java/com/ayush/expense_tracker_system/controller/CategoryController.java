package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.CreateCategoryRequest;
import com.ayush.expense_tracker_system.dto.request.UpdateCategoryRequest;
import com.ayush.expense_tracker_system.dto.response.CategoryResponse;
import com.ayush.expense_tracker_system.dto.response.ErrorResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.CannotCreateGlobalCategoryException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryAlreadyExistsException;
import com.ayush.expense_tracker_system.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest categoryRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryRequest));
    }

    @GetMapping("/")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.updateCategory(id, updateCategoryRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive  Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCategoryException(Exception exception){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CannotCreateGlobalCategoryException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorisedCategoryCreation(Exception exception){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }



}
