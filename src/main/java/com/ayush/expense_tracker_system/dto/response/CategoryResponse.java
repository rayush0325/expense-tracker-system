package com.ayush.expense_tracker_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryResponse {
    private final Long id;
    private final String name;
    private final Long userId;
}
