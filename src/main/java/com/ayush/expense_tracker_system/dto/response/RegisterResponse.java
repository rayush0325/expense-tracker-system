package com.ayush.expense_tracker_system.dto.response;

import com.ayush.expense_tracker_system.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
