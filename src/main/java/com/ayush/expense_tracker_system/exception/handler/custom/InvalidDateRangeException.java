package com.ayush.expense_tracker_system.exception.handler.custom;

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
