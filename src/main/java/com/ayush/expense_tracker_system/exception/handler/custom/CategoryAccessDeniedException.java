package com.ayush.expense_tracker_system.exception.handler.custom;

public class CategoryAccessDeniedException extends RuntimeException {
    public CategoryAccessDeniedException(String message) {
        super(message);
    }
}
