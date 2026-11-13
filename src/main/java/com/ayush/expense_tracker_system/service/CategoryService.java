package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.CreateCategoryRequest;
import com.ayush.expense_tracker_system.dto.response.CategoryResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.CannotCreateGlobalCategoryException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryAlreadyExistsException;
import com.ayush.expense_tracker_system.model.Category;
import com.ayush.expense_tracker_system.model.CustomUserDetails;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.CategoryRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CreateCategoryRequest categoryRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        String categoryName = categoryRequest.getName().trim();

        //check if creation of category is allowed
        if (categoryRequest.isGlobal() && user.getRole() == Role.USER) {
            throw new CannotCreateGlobalCategoryException("only admins can create global category");
        }

        //check if duplicate category exists
        boolean isDuplicate = categoryRequest.isGlobal()
                ? categoryRepository.existsByNameIgnoreCaseAndUserIsNull(categoryName)
                : categoryRepository.existsByNameIgnoreCaseAndUser_Id(categoryName, user.getId());

        if (isDuplicate) {
            throw new CategoryAlreadyExistsException("category already exists");
        }

        //create category
        User categoryOwner = categoryRequest.isGlobal() ? null : user;
        Category savedCategory = categoryRepository.save(new Category(categoryName, categoryOwner));
        return new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getUser() == null ? null : savedCategory.getUser().getId()
        );

    }
}
