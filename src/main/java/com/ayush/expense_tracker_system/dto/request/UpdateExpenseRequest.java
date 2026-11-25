package com.ayush.expense_tracker_system.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class UpdateExpenseRequest {
    @Positive(message = "amount cannot be negative")
    private BigDecimal amount;

    @Pattern(regexp = "^(?!\\s*$).+",message = "description cannot be empty")
    private String description;

    @PastOrPresent(message = "expense date cannot be in future")
    private LocalDate expenseDate;

    @Positive(message = "category must be positive")
    private Long categoryId;
}
