package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.RegisterResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.EmailAlreadyExistsException;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
public class UserServiceIntegrationTest {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserServiceIntegrationTest(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @Test
    @Transactional
    void shouldRegisterUser(){
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");

        RegisterResponse response = userService.registerUser(registerRequest);


        assertThat(userRepository.existsByEmail(registerRequest.getEmail())).isTrue();
        assertThat(userRepository.findById(response.getId()).get().getPassword()).isNotEqualTo(registerRequest.getPassword());
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo(registerRequest.getName());
        assertThat(response.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(response.getRole()).isEqualTo(Role.USER);



    }

    @Test
    @Transactional
    void shouldThrowEmailAlreadyExistsException(){
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");
        userService.registerUser(registerRequest);

        RegisterRequest duplicateRequest = new RegisterRequest("tester", "test@mail.com", "test@123");

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(duplicateRequest)
        );


    }
}
