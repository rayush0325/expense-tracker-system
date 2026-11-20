package com.ayush.expense_tracker_system.repository;

import com.ayush.expense_tracker_system.model.Expense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends CrudRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
}
