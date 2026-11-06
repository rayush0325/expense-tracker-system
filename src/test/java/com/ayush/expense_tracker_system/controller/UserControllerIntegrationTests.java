package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.dto.response.UserResponse;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.UserRepository;
import com.ayush.expense_tracker_system.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearRepository() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldRegisterUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");
        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value(registerRequest.getEmail()))
                .andExpect(jsonPath("$.name").value(registerRequest.getName()))
                .andExpect(jsonPath("$.role").value(Role.USER.name()))

        ;

        User savedUser = userRepository.findByEmail(registerRequest.getEmail());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(savedUser.getName()).isEqualTo(registerRequest.getName());
        assertThat(savedUser.getPassword()).isNotEqualTo(registerRequest.getPassword());
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @Transactional
    void shouldReturnEmailAlreadyExistsException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");

        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isCreated());

        RegisterRequest duplicateRequest = new RegisterRequest("tester2", "test@mail.com", "test@123");
        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(duplicateRequest))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("email already exists"));
    }

    @Test
    @Transactional
    void shouldLoginWhenRegisteredUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");
        userService.registerUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest("test@mail.com", "test@123");

        String response = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        System.out.printf("\n\n\nlogin response = %s\n\n\n", loginResponse.getJwtToken());

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void shouldNotLoginWhenUnregisteredUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest("unregistered@mail.com", "test@123");

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("invalid email or password"))
        ;
    }

    @Test
    @Transactional
    void shouldNotLoginWhenWrongPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");
        userService.registerUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest("test@mail.com", "wrong@123");

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("invalid email or password"))
        ;
    }

    @Test
    void shouldGetProfileWhenValidToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("tester", "test@mail.com", "test@123");
        userService.registerUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest("test@mail.com", "test@123");

        String response = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        System.out.printf("\n\n\nlogin response = %s\n\n\n", loginResponse.getJwtToken());

        mockMvc.perform(get("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(registerRequest.getName()))
                .andExpect(jsonPath("$.email").value(registerRequest.getEmail()))
                .andExpect(jsonPath("$.role").value(Role.USER.name()));
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value(registerRequest.getName())
//                .andExpect(jsonPath("$.email").value(registerRequest.getEmail());
//                .andExpect(jsonPath("$.role").value(Role.ROLE_USER.name())
//                .andDo(print());
    }

    @Test
    void shouldNotGetProfileWhenInvalidToken() throws Exception {

        mockMvc.perform(get("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "invalid.jwt.token")
                )
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.title").value("authentication failed"))
                .andDo(print());
    }

    @Test
    void shouldNotGetProfileWhenEmptyToken() throws Exception {

        mockMvc.perform(get("/api/v1/user/profile")
                                .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", "")
                )
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.title").value("authentication failed"))
                .andDo(print());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        User user1 = new User("tester1@mail.com", "tester1", "tester1@123", Role.USER);
        User user2 = new User("tester2@mail.com", "tester2", "tester2@123", Role.USER);
        User admin = new User("admin@mail.com", "admin", "admin@123", Role.ADMIN);

        List<User> userList = List.of(user1, user2, admin);
        userList.forEach(user -> {
            userService.registerUser(new RegisterRequest(user.getName(), user.getEmail(), user.getPassword()));

        });
        User savedAdmin = userRepository.findByEmail(admin.getEmail());
        savedAdmin.setRole(Role.ADMIN);
        userRepository.save(savedAdmin);


        LoginRequest loginRequest = new LoginRequest(admin.getEmail(), admin.getPassword());

        String response = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();
//
        String response1 = mockMvc.perform(get("/api/v1/admin/users/")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

//
        List<UserResponse> userResponses = objectMapper.readValue(
                response1,
                new TypeReference<List<UserResponse>>() {
                }
        );
        Map<String, UserResponse> userResponseMap = userResponses.stream()
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
    void shouldReturn403WhenUserAccessesAdminEndpoint   () throws Exception {
        User user = new User("tester1@mail.com", "tester1", "tester1@123", Role.USER);
        userService.registerUser(new RegisterRequest(user.getName(), user.getEmail(), user.getPassword()));

        LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());

        String response = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();
        mockMvc.perform(get("/api/v1/admin/users/")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    void shouldDeleteExistingUser() throws Exception {
        User user = new User("tester1@mail.com", "tester1", "tester1@123", Role.USER);
        User admin = new User("admin@mail.com", "admin", "admin@123", Role.ADMIN);

        Long userId = userService.registerUser(new RegisterRequest(user.getName(), user.getEmail(), user.getPassword()))
                .getId();



        userService.registerUser(new RegisterRequest(admin.getName(), admin.getEmail(), admin.getPassword()));

        User savedAdmin = userRepository.findByEmail(admin.getEmail());
        savedAdmin.setRole(Role.ADMIN);
        userRepository.save(savedAdmin);

        LoginRequest loginRequest = new LoginRequest(admin.getEmail(), admin.getPassword());

        String response = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();

        mockMvc.perform(delete(String.format("/api/v1/admin/users/%d", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+ jwtToken))

                .andExpect(status().is(204));

        assertThat(userRepository.existsByEmail(user.getEmail())).isFalse();

    }

    @Test
    void shouldThrowErrorOnDeletingNonExistingUser() throws Exception {
        User admin = new User("admin@mail.com", "admin", "admin@123", Role.ADMIN);

        userService.registerUser(new RegisterRequest(admin.getName(), admin.getEmail(), admin.getPassword()));

        User savedAdmin = userRepository.findByEmail(admin.getEmail());
        savedAdmin.setRole(Role.ADMIN);
        userRepository.save(savedAdmin);

        LoginRequest loginRequest = new LoginRequest(admin.getEmail(), admin.getPassword());

        String response = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();

        mockMvc.perform(delete(String.format("/api/v1/admin/users/%d", 0L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+ jwtToken))

                .andExpect(status().is(404));
    }


}
