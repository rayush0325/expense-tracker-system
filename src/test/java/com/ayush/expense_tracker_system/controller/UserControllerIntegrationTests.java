package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        User savedUser = userRepository.findByEmail(registerRequest.getEmail()).orElse(null);

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
}
