package com.ayush.expense_tracker_system.dto.response;

public class ErrorResponse {
    private String title;

    public ErrorResponse(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
