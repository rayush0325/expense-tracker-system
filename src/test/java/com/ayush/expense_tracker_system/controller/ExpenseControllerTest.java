package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.CreateExpenseRequest;
import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.UpdateExpenseRequest;
import com.ayush.expense_tracker_system.dto.response.ExpenseResponse;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.model.Category;
import com.ayush.expense_tracker_system.model.Expense;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.CategoryRepository;
import com.ayush.expense_tracker_system.repository.ExpenseRepository;
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
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ExpenseControllerTest {
    private User user = new User("tester@mail.com", "tester", "test@123", Role.USER);
    private User user2 = new User("tester2@mail.com", "tester2", "test2@123", Role.USER);
    private User savedUser, savedUser2;
    private Category savedCategory, globalSavedCategory, user2SavedCategory;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExpenseRepository expenseRepository;

    @BeforeEach
    void setup() {
        savedUser = userRepository.save(new User("tester@mail.com", "tester", passwordEncoder.encode("test@123"), Role.USER));
        savedUser2 = userRepository.save(new User("tester2@mail.com", "tester2", passwordEncoder.encode("test@123"), Role.USER));
        savedCategory = categoryRepository.save(new Category("food", savedUser));
        user2SavedCategory = categoryRepository.save(new Category("shop", savedUser2));
        globalSavedCategory = categoryRepository.save(new Category("travel", null));
    }

    @Test
    void shouldCreateExpense() throws Exception {
        LoginResponse loginResponse = login(user);

        CreateExpenseRequest createExpenseRequest = new CreateExpenseRequest();
        createExpenseRequest.setAmount(BigDecimal.valueOf(250L));
        createExpenseRequest.setDescription("lunch");
        createExpenseRequest.setExpenseDate(LocalDate.of(2026, 11, 21));
        createExpenseRequest.setCategoryId(savedCategory.getId());

        mockMvc.perform(post("/api/v1/expenses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExpenseRequest))
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(250L)))
                .andExpect(jsonPath("$.description").value(createExpenseRequest.getDescription()))
                .andExpect(jsonPath("$.categoryId").value(createExpenseRequest.getCategoryId()))
                .andExpect(jsonPath("$.userId").value(savedUser.getId()));
    }

    @Test
    void shouldCreateExpenseWithGlobalCategory() throws Exception {
        LoginResponse loginResponse = login(user);

        CreateExpenseRequest createExpenseRequest = new CreateExpenseRequest();
        createExpenseRequest.setAmount(BigDecimal.valueOf(250L));
        createExpenseRequest.setDescription("lunch");
        createExpenseRequest.setCategoryId(globalSavedCategory.getId());

        mockMvc.perform(post("/api/v1/expenses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExpenseRequest))
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(250L)))
                .andExpect(jsonPath("$.expenseDate").value(LocalDate.now()+""))
                .andExpect(jsonPath("$.description").value(createExpenseRequest.getDescription()))
                .andExpect(jsonPath("$.categoryId").value(createExpenseRequest.getCategoryId()))
                .andExpect(jsonPath("$.userId").value(savedUser.getId()));
    }

    @Test
    void shouldThrowCategoryNotFoundException() throws Exception {
        LoginResponse loginResponse = login(user);
        CreateExpenseRequest createExpenseRequest = new CreateExpenseRequest();
        createExpenseRequest.setAmount(BigDecimal.valueOf(250L));
        createExpenseRequest.setDescription("lunch");
        createExpenseRequest.setExpenseDate(LocalDate.of(2026, 11, 21));
        createExpenseRequest.setCategoryId(Long.MAX_VALUE);

        mockMvc.perform(post("/api/v1/expenses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExpenseRequest))
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("category not found"));
    }

    @Test
    void shouldThrowCannotAccessCategoryException() throws Exception {
        LoginResponse loginResponse = login(user);

        CreateExpenseRequest createExpenseRequest = new CreateExpenseRequest();
        createExpenseRequest.setAmount(BigDecimal.valueOf(250L));
        createExpenseRequest.setDescription("lunch");
        createExpenseRequest.setExpenseDate(LocalDate.of(2026, 11, 21));
        createExpenseRequest.setCategoryId(user2SavedCategory.getId());

        mockMvc.perform(post("/api/v1/expenses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExpenseRequest))
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken())
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("category access denied"));
    }

    @Test
    void shouldGetAllExpenses() throws Exception {
        List<Expense> expenseList = List.of(
                new Expense("lunch", BigDecimal.valueOf(250L), LocalDate.of(2026, 11, 21), savedCategory, savedUser),
                new Expense("dinner", BigDecimal.valueOf(6250L), LocalDate.of(2025, 12, 15), globalSavedCategory, savedUser),
                new Expense("ride", BigDecimal.valueOf(123L), LocalDate.of(2023, 6, 7), savedCategory, savedUser),
                new Expense("pizza", BigDecimal.valueOf(2425L), LocalDate.of(2024, 8, 10), globalSavedCategory, savedUser)
        );

        expenseList.forEach(expense -> {
            Expense savedExpense = expenseRepository.save(expense);
            expense.setId(savedExpense.getId());
        });

        LoginResponse loginResponse = login(user);

        String response = mockMvc.perform(get("/api/v1/expenses/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + loginResponse.getJwtToken())
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ExpenseResponse> expenseResponseList = objectMapper.readValue(
                response,
                new TypeReference<List<ExpenseResponse>>() {}
        );
        Map<Long, ExpenseResponse> expenseResponseMap = new HashMap<>();
        expenseResponseList.forEach(expenseResponse -> expenseResponseMap.put(expenseResponse.getId(), expenseResponse));

        expenseList.forEach(
                expense -> {
                    assertThat(expenseResponseMap.containsKey(expense.getId())).isTrue();
                    ExpenseResponse expenseResponse = expenseResponseMap.get(expense.getId());

                    assertThat(expenseResponse.getAmount()).isEqualTo(expense.getAmount());
                    assertThat(expenseResponse.getDescription()).isEqualTo(expense.getDescription());
                    assertThat(expenseResponse.getCategoryId()).isEqualTo(expense.getCategory().getId());
                    assertThat(expenseResponse.getUserId()).isEqualTo(expense.getUser().getId());
                }
        );
    }

    @Test
    void shouldUpdateExpense() throws Exception {
        LoginResponse loginResponse = login(user);

        Expense expense = new Expense("lunch", BigDecimal.valueOf(250L), LocalDate.of(2026, 11, 21), savedCategory, savedUser);
        Expense savedExpense = expenseRepository.save(expense);

        UpdateExpenseRequest updateExpenseRequest = new UpdateExpenseRequest();
        updateExpenseRequest.setExpenseDate(LocalDate.of(2025, 10, 20));
        updateExpenseRequest.setCategoryId(globalSavedCategory.getId());

        mockMvc.perform(put(String.format("/api/v1/expenses/%d",savedExpense.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateExpenseRequest))
                .header("Authorization", "Bearer " + loginResponse.getJwtToken())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExpense.getId()))
                .andExpect(jsonPath("$.amount").value(savedExpense.getAmount()))
                .andExpect(jsonPath("$.description").value(savedExpense.getDescription()))
                .andExpect(jsonPath("$.expenseDate").value(updateExpenseRequest.getExpenseDate()+""))
                .andExpect(jsonPath("$.categoryId").value(updateExpenseRequest.getCategoryId()));
    }

    @Test
    void shouldThrowExpenseNotFoundException() throws Exception {
        LoginResponse loginResponse = login(user);

        UpdateExpenseRequest updateExpenseRequest = new UpdateExpenseRequest();

        mockMvc.perform(put(String.format("/api/v1/expenses/%d",1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateExpenseRequest))
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("expense not found"));

    }

    @Test
    void shouldThrowExpenseAccessDeniedException() throws Exception {
        LoginResponse loginResponse = login(user);

        Expense expense = new Expense("lunch", BigDecimal.valueOf(250L), LocalDate.of(2026, 11, 21), savedCategory, savedUser2);
        Expense savedExpense = expenseRepository.save(expense);

        UpdateExpenseRequest updateExpenseRequest = new UpdateExpenseRequest();

        mockMvc.perform(put(String.format("/api/v1/expenses/%d",savedExpense.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateExpenseRequest))
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken())
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("expense access denied"));
    }

    @Test
    void shouldDeleteExpense() throws Exception {
        LoginResponse loginResponse = login(user);

        Expense expense1 = expenseRepository.save(new Expense("lunch", BigDecimal.valueOf(250L), LocalDate.of(2026, 11, 21), savedCategory, savedUser));
        Expense expense2 = expenseRepository.save(new Expense("dinner", BigDecimal.valueOf(650L), LocalDate.of(2026, 11, 21), savedCategory, savedUser));

        mockMvc.perform(delete(String.format("/api/v1/expenses/%d",expense1.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + loginResponse.getJwtToken())
        )
                .andExpect(status().isNoContent());

        assertThat(expenseRepository.existsById(expense1.getId())).isFalse();
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

}
