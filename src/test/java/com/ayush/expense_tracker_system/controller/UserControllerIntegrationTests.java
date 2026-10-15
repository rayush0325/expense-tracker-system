package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void clearRepository(){
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
                    .andExpect(jsonPath("$.role").value(Role.ROLE_USER.name()))

            ;

        User savedUser = userRepository.findByEmail(registerRequest.getEmail());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(savedUser.getName()).isEqualTo(registerRequest.getName());
        assertThat(savedUser.getPassword()).isNotEqualTo(registerRequest.getPassword());
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
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
                .andExpect(jsonPath("$.name").value("profile from protected endpoint"))
                .andDo(print());
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



}
