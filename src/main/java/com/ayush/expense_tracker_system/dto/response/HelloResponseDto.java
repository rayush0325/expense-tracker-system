package com.ayush.expense_tracker_system.dto.response;

public class HelloResponseDto {
    private final String message;

    public HelloResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
