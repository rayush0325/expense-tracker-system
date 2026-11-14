package com.ayush.expense_tracker_system.exception.handler.custom;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
