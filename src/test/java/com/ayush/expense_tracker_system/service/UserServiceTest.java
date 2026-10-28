package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.dto.response.RegisterResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.EmailAlreadyExistsException;
import com.ayush.expense_tracker_system.model.CustomUserDetails;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



public class UserServiceTest {
    private final UserService userService;
    UserRepository mockUserRepository;
    PasswordEncoder mockPasswordEncoder;
    JwtService mockJwtService;
    AuthenticationManager mockAuthenticationManager;


    public UserServiceTest() {
        mockUserRepository = mock(UserRepository.class);
        mockPasswordEncoder = mock(PasswordEncoder.class);
        mockJwtService = mock(JwtService.class);
        mockAuthenticationManager = mock(AuthenticationManager.class);
        this.userService = new UserService(mockUserRepository, mockPasswordEncoder, mockJwtService, mockAuthenticationManager);
    }

    @Test
    void shouldSaveUser(){

        RegisterRequest registerRequest = new RegisterRequest(
                "ayush",
                "test@mail.com",
                "test@123"
        );
        final String encodePassword = "encodedtest@123";

        when(mockPasswordEncoder.encode(registerRequest.getPassword())).thenReturn(encodePassword);

        User newUser = new User(registerRequest.getEmail(), registerRequest.getName(), encodePassword, Role.ROLE_USER);
        User savedUser = new User(1L, "test@mail.com", "ayush", encodePassword, Role.ROLE_USER);
            when(mockUserRepository.save(newUser))
                    .thenReturn(savedUser);

        RegisterResponse response =  userService.registerUser(registerRequest);

        verify(mockUserRepository).existsByEmail(registerRequest.getEmail());
        verify(mockPasswordEncoder).encode(registerRequest.getPassword());
        verify(mockUserRepository).save(newUser);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(response.getName()).isEqualTo(registerRequest.getName());
        assertThat(response.getRole()).isEqualTo(Role.ROLE_USER);

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

    @Test
    void shouldLoginUserWhenValidCredentials(){

        User user = new User("test@mail.com", "tester", "test@123", Role.ROLE_USER);
        LoginRequest loginRequest = new LoginRequest(
                user.getEmail(),
                user.getPassword()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );

        when(mockAuthenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        when(mockJwtService.generateJwtToken(user))
                .thenReturn("jwt_token");



        LoginResponse loginResponse = userService.authenticateUser(loginRequest);

        verify(mockAuthenticationManager).authenticate(any(Authentication.class));

        verify(mockJwtService).generateJwtToken(user);

        assertThat(loginResponse.getJwtToken()).isEqualTo("jwt_token");

    }

    @Test
    void shouldNotLoginWhenInvalidCredentials(){
        User user = new User("test@mail.com", "tester", "test@123", Role.ROLE_USER);
        LoginRequest loginRequest = new LoginRequest(
                user.getEmail(),
                user.getPassword()
        );

        when(mockAuthenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("authentication failed"));




        assertThrows(
                AuthenticationException.class,
                () -> userService.authenticateUser(loginRequest)
        );
        verify(mockAuthenticationManager).authenticate(any(Authentication.class));
        verify(mockJwtService, never()).generateJwtToken(user);
    }




}
