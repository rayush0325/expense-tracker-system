package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.CreateCategoryRequest;
import com.ayush.expense_tracker_system.dto.response.CategoryResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.CannotCreateGlobalCategoryException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryAlreadyExistsException;
import com.ayush.expense_tracker_system.model.Category;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    public CategoryService(CategoryRepository categoryRepository, CurrentUserService currentUserService) {
        this.categoryRepository = categoryRepository;
        this.currentUserService = currentUserService;
    }

    public CategoryResponse createCategory(CreateCategoryRequest categoryRequest) {
//        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = currentUserService.getCurrentUser();
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

    public List<CategoryResponse> getAllCategories() {
        User user = currentUserService.getCurrentUser();

        List<Category> categories = categoryRepository.findByUserIdOrUserIdIsNull(user.getId());

        return categories.stream()
                .map(category -> {
                    Long categoryUserId = category.getUser() == null ? null : category.getUser().getId();
                    return new CategoryResponse(category.getId(), category.getName(), categoryUserId);
                }).collect(Collectors.toList());
    }
}
