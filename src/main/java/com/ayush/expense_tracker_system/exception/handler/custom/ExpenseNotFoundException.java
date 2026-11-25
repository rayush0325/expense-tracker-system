package com.ayush.expense_tracker_system.exception.handler.custom;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException(String message) {
        super(message);
    }
}
