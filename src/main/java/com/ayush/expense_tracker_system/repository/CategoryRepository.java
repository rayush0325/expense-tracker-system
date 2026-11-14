package com.ayush.expense_tracker_system.repository;

import com.ayush.expense_tracker_system.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findById(Long id);
    List<Category> findByUserIdOrUserIdIsNull(Long userId);

    boolean existsByNameIgnoreCaseAndUser_Id(String name, Long userId);
    boolean existsByNameIgnoreCaseAndUserIsNull(String name);
    boolean existsByNameIgnoreCaseAndUser_IdAndIdNot(String newCategoryName, Long userId, Long categoryId);
}
