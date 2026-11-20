package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.CreateExpenseRequest;
import com.ayush.expense_tracker_system.dto.response.ExpenseResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryAccessDeniedException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryNotFoundException;
import com.ayush.expense_tracker_system.model.Category;
import com.ayush.expense_tracker_system.model.Expense;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.CategoryRepository;
import com.ayush.expense_tracker_system.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CurrentUserService currentUserService;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository, CurrentUserService currentUserService, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.currentUserService = currentUserService;
        this.categoryRepository = categoryRepository;
    }

    public ExpenseResponse createExpense(CreateExpenseRequest createExpenseRequest) {
        User currentUser = currentUserService.getCurrentUser();
        Category selectedCategory = getCategoryOrThrow(createExpenseRequest.getCategoryId());
        validateCategoryAccess(selectedCategory, currentUser);

        LocalDate expenseDate = createExpenseRequest.getExpenseDate();
        if(expenseDate == null){
            expenseDate = LocalDate.now();
        }


        Expense expense = new Expense(
                createExpenseRequest.getDescription(),
                createExpenseRequest.getAmount(),
                expenseDate,
                selectedCategory,
                currentUserService.getCurrentUser()
        );

        return mapToExpenseResponse(expenseRepository.save(expense));

    }

    private void validateCategoryAccess(Category category, User user) {
        if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
            throw new CategoryAccessDeniedException("you are not allowed to access this category");
        }
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category not found"));
    }

    private ExpenseResponse mapToExpenseResponse(Expense expense){
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getCategory().getId(),
                expense.getUser().getId()
        );
    }
}
