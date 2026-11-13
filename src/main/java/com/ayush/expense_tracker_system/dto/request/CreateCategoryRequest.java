package com.ayush.expense_tracker_system.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateCategoryRequest {

    @NotBlank(message = "category name should not be blank")
    @Size(max = 50, message = "category name length must not exceed 50 characters")
    private String name;

    private boolean global = false;

}
