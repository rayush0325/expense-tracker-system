package com.ayush.expense_tracker_system.exception.handler.custom;

public class CannotCreateGlobalCategoryException extends RuntimeException {
    public CannotCreateGlobalCategoryException(String message) {
        super(message);
    }
}
