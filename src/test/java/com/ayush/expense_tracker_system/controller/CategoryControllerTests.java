package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.CreateCategoryRequest;
import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.model.Category;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.CategoryRepository;
import com.ayush.expense_tracker_system.repository.UserRepository;
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
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CategoryControllerTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private User admin;

    @BeforeEach
    void setUpRepository() {
        user = new User("tester@mail.com", "tester", "tester@123", Role.USER);
        admin = new User("admin@mail.com", "admin", "admin@123", Role.ADMIN);
        saveUsers();
        saveCategories();

    }


    private void saveUsers() {
        List<User> userList = List.of(user, admin);
        userList.forEach(user -> {
            User savedUser = userRepository.save(new User(user.getEmail(), user.getName(), passwordEncoder.encode(user.getPassword()), user.getRole()));
            user.setId(savedUser.getId());
        });
    }

    private void saveCategories() {

        List<String> userCategories = List.of("study", "sport");
        List<String> adminCategories = List.of("gym", "electronics");
        List<String> globalCategories = List.of("food", "travel");

        userCategories.forEach(categoryName -> categoryRepository.save(new Category(categoryName, user)));
        adminCategories.forEach(categoryName -> categoryRepository.save(new Category(categoryName, admin)));
        globalCategories.forEach(categoryName -> categoryRepository.save(new Category(categoryName, null)));
    }

    private LoginResponse login(User user) throws Exception {
        LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());

        String response = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, LoginResponse.class);
    }

    @Test
    void shouldCreateCategoryWhenUserCreateNonDuplicateCategory() throws Exception {
        String jwtToken = login(user).getJwtToken();

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("yoga");

        mockMvc.perform(post("/api/v1/categories/")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("yoga"))
                .andExpect(jsonPath("$.userId").value(user.getId()));
        assertThat(categoryRepository.existsByNameIgnoreCaseAndUser_Id("yoga", user.getId())).isTrue();
    }

    @Test
    void shouldCreateCategoryWhenAdminCreateNonDuplicateGlobalCategory() throws Exception {
        String jwtToken = login(admin).getJwtToken();

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("yoga");
        categoryRequest.setGlobal(true);

        mockMvc.perform(post("/api/v1/categories/")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("yoga"))
                .andExpect(jsonPath("$.userId").isEmpty());
        assertThat(categoryRepository.existsByNameIgnoreCaseAndUserIsNull(categoryRequest.getName())).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenUserCreatesDuplicateCategory() throws Exception {
        String jwtToken = login(user).getJwtToken();

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("STUDY");

        mockMvc.perform(post("/api/v1/categories/")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("category already exists"));


    }

    @Test
    void shouldThrowExceptionWhenAdminCreatesDuplicateGlobalCategory() throws Exception {
        String jwtToken = login(admin).getJwtToken();

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("FOOD");
        categoryRequest.setGlobal(true);

        mockMvc.perform(post("/api/v1/categories/")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("category already exists"));

    }

    @Test
    void shouldThrowExceptionWhenUserCreatesGlobalCategory() throws Exception {
        String jwtToken = login(user).getJwtToken();

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("STUDY");
        categoryRequest.setGlobal(true);

        mockMvc.perform(post("/api/v1/categories/")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("only admins can create global category"));
        assertThat(categoryRepository.existsByNameIgnoreCaseAndUserIsNull("STUDY")).isFalse();
    }
}
