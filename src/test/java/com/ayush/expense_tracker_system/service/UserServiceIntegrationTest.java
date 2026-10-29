package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.dto.response.RegisterResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.EmailAlreadyExistsException;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
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

    @BeforeEach
    void emptyUserRepository(){
        userRepository.deleteAll();
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
        assertThat(response.getRole()).isEqualTo(Role.ROLE_USER);



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

    @Test
    @Transactional
    void shouldLoginUser(){
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");
        userService.registerUser(registerRequest);
        LoginRequest loginRequest = new LoginRequest("test@mail.com", "test@123");

        LoginResponse loginResponse = userService.authenticateUser(loginRequest);

        assertThat(loginResponse.getJwtToken()).isNotBlank();
    }

    @Test
    @Transactional
    void shouldNotLoginWhenInvalidEmail(){
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");
        userService.registerUser(registerRequest);
        LoginRequest invalidEmailRequest = new LoginRequest("invalid@mail.com", "test@123");

        assertThrows(AuthenticationException.class, () -> {userService.authenticateUser(invalidEmailRequest);});
    }

    @Test
    @Transactional
    void shouldNotLoginWhenInvalidPassword(){
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");
        userService.registerUser(registerRequest);
        LoginRequest invalidPasswordRequest = new LoginRequest("test@mail.com", "invalid@123");

        assertThrows(AuthenticationException.class, () -> {userService.authenticateUser(invalidPasswordRequest);});
    }
}
