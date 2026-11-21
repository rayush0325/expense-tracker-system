package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.CreateExpenseRequest;
import com.ayush.expense_tracker_system.dto.request.UpdateExpenseRequest;
import com.ayush.expense_tracker_system.dto.response.ExpenseResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryAccessDeniedException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryNotFoundException;
import com.ayush.expense_tracker_system.exception.handler.custom.ExpenseAccessDeniedException;
import com.ayush.expense_tracker_system.exception.handler.custom.ExpenseNotFoundException;
import com.ayush.expense_tracker_system.model.Category;
import com.ayush.expense_tracker_system.model.Expense;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.CategoryRepository;
import com.ayush.expense_tracker_system.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    public ExpenseResponse updateExpense(UpdateExpenseRequest updateExpenseRequest, Long id){

        Expense selectedExpense = getExpenseOrThrow(id);

        User user = currentUserService.getCurrentUser();
        validateExpenseAccess(user, selectedExpense);

        if (updateExpenseRequest.getAmount() != null){
            selectedExpense.setAmount(updateExpenseRequest.getAmount());
        }
        if (updateExpenseRequest.getDescription() != null){
            selectedExpense.setDescription(updateExpenseRequest.getDescription());
        }
        if (updateExpenseRequest.getExpenseDate() != null){
            selectedExpense.setExpenseDate(updateExpenseRequest.getExpenseDate());
        }
        if (updateExpenseRequest.getCategoryId() != null){
            Category newCategory = getCategoryOrThrow(updateExpenseRequest.getCategoryId());
            validateCategoryAccess(newCategory, user);
            selectedExpense.setCategory(newCategory);
        }

        return mapToExpenseResponse(
                expenseRepository.save(selectedExpense)
        );
    }

    public List<ExpenseResponse> getAllExpenses(){
        User user = currentUserService.getCurrentUser();

        return expenseRepository.findByUserId(user.getId())
                .stream()
                .map(expense -> mapToExpenseResponse(expense))
                .toList();
    }

    public void deleteExpense(Long id){
        Expense selectedExpense = getExpenseOrThrow(id);
        validateExpenseAccess(currentUserService.getCurrentUser(), selectedExpense);

        expenseRepository.delete(selectedExpense);
    }

    private void validateCategoryAccess(Category category, User user) {
        if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
            throw new CategoryAccessDeniedException("category access denied");
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

    private Expense getExpenseOrThrow(Long id){
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("expense not found"));
    }

    private void validateExpenseAccess(User user, Expense expense){
        if (!expense.getUser().getId().equals(user.getId())){
            throw new ExpenseAccessDeniedException("expense access denied");
        }
    }
}
