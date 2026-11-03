package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.dto.response.RegisterResponse;
import com.ayush.expense_tracker_system.dto.response.UserResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.EmailAlreadyExistsException;
import com.ayush.expense_tracker_system.exception.handler.custom.UserNotFoundException;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Test
    @Transactional
    void shouldGetAllUsers(){

        User user1 = new User("tester1@mail.com", "tester1", "tester1@123", Role.USER);
        User user2 = new User("tester2@mail.com", "tester2", "tester2@123",Role.ADMIN);
        List<User> userList = List.of(user1, user2);
        userList.forEach(user -> userRepository.save(user));


        List<UserResponse> userResponses = userService.getAllUsers();
        Map<String, UserResponse> userResponseMap =  userResponses.stream()
                        .collect(Collectors.toMap(
                                userResponse -> userResponse.getEmail(),
                                userResponse -> userResponse
                        ));
        userList.forEach(
                user -> {
                    assertThat(userResponseMap.containsKey(user.getEmail())).isTrue();
                    UserResponse userResponse = userResponseMap.get(user.getEmail());
                    assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
                    assertThat(userResponse.getName()).isEqualTo(user.getName());
                    assertThat(userResponse.getRole()).isEqualTo(user.getRole());
                }
        );

    }
    @Test
    void shouldGetEmptyList(){
        List<UserResponse> userResponses = userService.getAllUsers();
        assertThat(userResponses).isEmpty();
    }

    @Test
    void shouldDeleteUserIfItExists(){
        User user = new User("tester", "test@mail.com", "test@123", Role.USER);
        Long id = userRepository.save(user).getId();

        userService.deleteUserById(id);

        assertThat(userRepository.existsById(id)).isFalse();
    }

    @Test
    void shouldThrowExceptionOnDeletingNonExistingUser(){
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1L));
    }
}
