package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.CreateExpenseRequest;
import com.ayush.expense_tracker_system.dto.request.UpdateExpenseRequest;
import com.ayush.expense_tracker_system.dto.response.ErrorResponse;
import com.ayush.expense_tracker_system.dto.response.ExpenseResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.ExpenseAccessDeniedException;
import com.ayush.expense_tracker_system.exception.handler.custom.ExpenseNotFoundException;
import com.ayush.expense_tracker_system.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/")
    public ResponseEntity<ExpenseResponse> createExpense(@RequestBody @Valid CreateExpenseRequest createExpenseRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(expenseService.createExpense(createExpenseRequest));
    }

    @GetMapping("/")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(expenseService.getAllExpenses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id, @RequestBody @Valid UpdateExpenseRequest updateExpenseRequest){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(expenseService.updateExpense(updateExpenseRequest, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpense(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExpenseNotFound(ExpenseNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ExpenseAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleExpenseAccessDenied(ExpenseAccessDeniedException exception){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }
}
