package com.ayush.expense_tracker_system.security;

import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
public class JwtAuthFilterIntegrationTests {
    private final UserService userService;
    private final JwtAuthFilter jwtAuthFilter;

    public JwtAuthFilterIntegrationTests(UserService userService, JwtAuthFilter jwtAuthFilter) {
        this.userService = userService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Test
    void shouldParseValidToken(){
        User user = new User("test@mail.com", "tester", "test@123", Role.ROLE_USER);
        RegisterRequest registerRequest = new RegisterRequest(user.getName(), user.getEmail(), user.getPassword());
        userService.registerUser(registerRequest);
        LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());
        LoginResponse loginResponse= userService.authenticateUser(loginRequest);

        String jwtToken = loginResponse.getJwtToken();


    }
}
