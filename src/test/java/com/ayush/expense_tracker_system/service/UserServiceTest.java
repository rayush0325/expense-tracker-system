package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.RegisterResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.EmailAlreadyExistsException;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



public class UserServiceTest {
    private final UserService userService;
    UserRepository mockUserRepository;
    PasswordEncoder mockPasswordEncoder;
    private final String MAIL = "test@mail.com";

    public UserServiceTest() {
        mockUserRepository = mock(UserRepository.class);
        mockPasswordEncoder = mock(PasswordEncoder.class);
        this.userService = new UserService(mockUserRepository, mockPasswordEncoder);
    }

    @Test
    void shouldSaveUser(){

        RegisterRequest registerRequest = new RegisterRequest(
                "ayush",
                MAIL,
                "test@123"
        );
        final String encodePassword = "encodedtest@123";

        when(mockPasswordEncoder.encode(registerRequest.getPassword())).thenReturn(encodePassword);

        User newUser = new User(registerRequest.getEmail(), registerRequest.getName(), encodePassword, Role.USER);
        User savedUser = new User(1L, MAIL, "ayush", encodePassword, Role.USER);
            when(mockUserRepository.save(newUser))
                    .thenReturn(savedUser);

        RegisterResponse response =  userService.registerUser(registerRequest);

        verify(mockUserRepository).existsByEmail(registerRequest.getEmail());
        verify(mockPasswordEncoder).encode(registerRequest.getPassword());
        verify(mockUserRepository).save(newUser);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(response.getName()).isEqualTo(registerRequest.getName());
        assertThat(response.getRole()).isEqualTo(Role.USER);

    }

    @Test
    void shouldThrowEmailAlreadyExistsException(){
        RegisterRequest duplicateRequest = new RegisterRequest(
                "tester2",
                "test@gmail.com",
                "test@456"
        );

        when(mockUserRepository.existsByEmail(duplicateRequest.getEmail())).thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(duplicateRequest)
        );
        verify(mockUserRepository, never()).save(any(User.class));
        verify(mockPasswordEncoder, never()).encode(any(String.class));
    }
}
