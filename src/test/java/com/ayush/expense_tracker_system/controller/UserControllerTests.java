package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.RegisterResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.EmailAlreadyExistsException;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class UserControllerTests {

    @MockitoBean
    private UserService mockUserService;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public UserControllerTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldCallRegisterUser() throws Exception {

        RegisterRequest registerRequest = new RegisterRequest("tester", "test@gmail.com", "test@123");

        when(mockUserService.registerUser(any(RegisterRequest.class)))
                .thenReturn(new RegisterResponse(1L, "tester", "test@gmail.com", Role.USER));


        mockMvc.perform(
                    post("/api/v1/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(registerRequest.getName()))
                .andExpect(jsonPath("$.email").value(registerRequest.getEmail()))
                .andExpect(jsonPath("$.role").value(Role.USER.toString()));



        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);

        verify(mockUserService).registerUser(captor.capture());

        RegisterRequest actualRequest = captor.getValue();

        assertThat(actualRequest.getName()).isEqualTo(registerRequest.getName());
        assertThat(actualRequest.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(actualRequest.getPassword()).isEqualTo(registerRequest.getPassword());

    }

    @Test
    void shouldReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                            "email" : "test@gmail.com",
                            "name" : " ",
                            "password" : "test@123"
                        }
                        """))
                .andDo(print())
                .andExpect(status().isBadRequest());
//                .andExpect(jsonPath("$.name").value("name must not be blank"));

        verify(mockUserService, never()).registerUser(any(RegisterRequest.class));
    }

    @Test
    void shouldHandleEmailAlreadyExistsException() throws Exception {
        when(mockUserService.registerUser(any(RegisterRequest.class))).thenThrow(new EmailAlreadyExistsException("email already exists"));

        mockMvc.perform(
                post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email" : "test@gmail.com",
                            "name" : "tester",
                            "password" : "test@123"
                        }
                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("title").value("email already exists"));

    }

}
