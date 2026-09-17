package com.ayush.expense_tracker_system.dto.response;

public class HelloResponse {
    private final String message;

    public HelloResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
