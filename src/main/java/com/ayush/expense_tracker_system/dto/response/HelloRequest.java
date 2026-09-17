package com.ayush.expense_tracker_system.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class HelloRequest {
    @NotNull(message = "message can't be null")
    @NotEmpty(message = "message can't be empty")
    @Size(min = 1, max = 10, message = "message length must be in range 1-10")
    private final String message;

    public HelloRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
