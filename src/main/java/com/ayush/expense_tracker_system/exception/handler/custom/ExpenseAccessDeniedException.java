package com.ayush.expense_tracker_system.exception.handler.custom;

public class ExpenseAccessDeniedException extends RuntimeException {
    public ExpenseAccessDeniedException(String message) {
        super(message);
    }
}
