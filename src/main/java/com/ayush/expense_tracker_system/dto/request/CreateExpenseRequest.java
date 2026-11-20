package com.ayush.expense_tracker_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class CreateExpenseRequest {
    @NotNull(message = "amount cannot be null")
    @Positive(message = "amount cannot be negative")
    private BigDecimal amount;

    @NotBlank(message = "description cannot be blank")
    private String description;

    @NotNull(message = "categoryId cannot be null")
    @Positive(message = "category must be positive")
    private Long categoryId;

    @NotNull(message = "expense date cannot be null")
    @PastOrPresent(message = "expense date cannot be in future")
    private LocalDate expenseDate;
}
