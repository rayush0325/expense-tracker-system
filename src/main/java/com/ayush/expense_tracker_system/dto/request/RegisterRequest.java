package com.ayush.expense_tracker_system.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "name must not be blank")
    @Size(min = 2, max = 50, message = "name must be in range 2 - 50")
    private String name;

    @NotBlank(message = "email must not be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "password must not be blank")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "password must contain both numbers and letters")
    @Size(min = 5, max = 20 ,message = "password should be in range 5 to 30")
    private String password;

}
