package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.CreateExpenseRequest;
import com.ayush.expense_tracker_system.dto.request.UpdateExpenseRequest;
import com.ayush.expense_tracker_system.dto.response.ExpenseResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryAccessDeniedException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryNotFoundException;
import com.ayush.expense_tracker_system.model.Category;
import com.ayush.expense_tracker_system.model.Expense;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.CategoryRepository;
import com.ayush.expense_tracker_system.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {
    private final User user = new User("tester@mail.com", "tester", "test@123", Role.USER);
    private final Category category = new Category("food", user);
    private final CurrentUserService currentUserService = mock(CurrentUserService.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final ExpenseService expenseService = new ExpenseService(expenseRepository, currentUserService, categoryRepository);

    @BeforeEach
    void setUp() {
        user.setId(1L);
        category.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);
    }

    @Test
    void shouldCreateExpense() {
        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        Expense savedExpense = new Expense("lunch", BigDecimal.valueOf(250L), LocalDate.now(), category, user);
        savedExpense.setId(1L);
        when(expenseRepository.save(any())).thenReturn(savedExpense);

        CreateExpenseRequest expenseRequest = new CreateExpenseRequest();
        expenseRequest.setAmount(BigDecimal.valueOf(250));
        expenseRequest.setDescription("lunch");
        expenseRequest.setExpenseDate(LocalDate.now());
        expenseRequest.setCategoryId(category.getId());

        ExpenseResponse expenseResponse = expenseService.createExpense(expenseRequest);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());

        Expense expense = captor.getValue();
        assertThat(expense.getAmount()).isEqualTo(expenseRequest.getAmount());
        assertThat(expense.getExpenseDate()).isEqualTo(expenseRequest.getExpenseDate());
        assertThat(expense.getCategory().getId()).isEqualTo(expenseRequest.getCategoryId());
        assertThat(expense.getUser().getId()).isEqualTo(user.getId());

        assertThat(expenseResponse.getId()).isEqualTo(1L);
        assertThat(expenseResponse.getAmount()).isEqualTo(BigDecimal.valueOf(250L));
        assertThat(expenseResponse.getDescription()).isEqualTo("lunch");
        assertThat(expenseResponse.getCategoryId()).isEqualTo(category.getId());
        assertThat(expenseResponse.getUserId()).isEqualTo(user.getId());

    }

    @Test
    void shouldGetCategoryNotFoundException(){
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
        CreateExpenseRequest expenseRequest = new CreateExpenseRequest();
        expenseRequest.setCategoryId(2L);

        assertThrows(CategoryNotFoundException.class, () -> expenseService.createExpense(expenseRequest));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldThrowCannotAccessCategoryException(){
        User anotherUser = new User();
        anotherUser.setId(2L);
        Category anotherUserCategory = new Category();
        anotherUserCategory.setId(2L);
        anotherUserCategory.setUser(anotherUser);

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(anotherUserCategory));
        CreateExpenseRequest expenseRequest = new CreateExpenseRequest();
        expenseRequest.setCategoryId(2L);

        assertThrows(CategoryAccessDeniedException.class, () -> expenseService.createExpense(expenseRequest));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldUpdateExpense(){
        Expense expense= new Expense("lunch", BigDecimal.valueOf(250L), LocalDate.now(), category, user);
        expense.setId(10L);

        when(expenseRepository.findById(10L)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any())).thenReturn(expense);

        UpdateExpenseRequest updateExpenseRequest = new UpdateExpenseRequest();
        updateExpenseRequest.setDescription("dinner");

        expenseService.updateExpense(updateExpenseRequest, 10L);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());

        Expense updatedExpense = captor.getValue();
        assertThat(updatedExpense.getDescription()).isEqualTo(updateExpenseRequest.getDescription());
        assertThat(updatedExpense.getAmount()).isEqualTo(expense.getAmount());
    }


}
