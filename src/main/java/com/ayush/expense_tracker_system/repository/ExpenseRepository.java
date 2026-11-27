package com.ayush.expense_tracker_system.repository;

import com.ayush.expense_tracker_system.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);

    @Query("""
                SELECT e FROM Expense e
                WHERE e.user.id = :userId
                AND(:categoryId IS NULL OR e.category.id = :categoryId)
                AND(:from IS NULL OR e.expenseDate >= :from)
                AND(:to IS NULL OR e.expenseDate <= :to)
            """)
    Page<Expense> filterExpenses(
            Pageable pageable,
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
