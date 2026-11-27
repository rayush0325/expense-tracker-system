package com.ayush.expense_tracker_system.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ExpenseFilterRequest {
    @Positive(message = "categoryId must be positive")
    private Long categoryId;

    @PastOrPresent(message = "date cannot be in future")
    private LocalDate from, to;


}
