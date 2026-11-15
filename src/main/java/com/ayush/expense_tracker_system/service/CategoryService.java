package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.CreateCategoryRequest;
import com.ayush.expense_tracker_system.dto.request.UpdateCategoryRequest;
import com.ayush.expense_tracker_system.dto.response.CategoryResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.CannotCreateGlobalCategoryException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryAccessDeniedException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryAlreadyExistsException;
import com.ayush.expense_tracker_system.exception.handler.custom.CategoryNotFoundException;
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

    private CategoryResponse mapToCategoryResponse(Category category) {
        Long userId = (category.getUser() != null) ? category.getUser().getId() : null;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                userId
        );
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
        return mapToCategoryResponse(savedCategory);

    }

    public List<CategoryResponse> getAllCategories() {
        User user = currentUserService.getCurrentUser();

        List<Category> categories = categoryRepository.findByUserIdOrUserIdIsNull(user.getId());

        return categories.stream()
                .map(category -> {
                    return mapToCategoryResponse(category);
                }).collect(Collectors.toList());
    }

    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest updateCategoryRequest) {
        User user = currentUserService.getCurrentUser();
        Category savedCategory = getCategoryOrThrow(id);

        validateCategoryOwnership(savedCategory);

        String newCategoryName = updateCategoryRequest.getName().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndUser_IdAndIdNot(newCategoryName, user.getId(), savedCategory.getId())) {
            throw new CategoryAlreadyExistsException("category with this name already exists");
        }
        savedCategory.setName(newCategoryName);

        return mapToCategoryResponse(categoryRepository.save(savedCategory));

    }


    public void deleteCategory(Long id) {
        Category savedCategory =  getCategoryOrThrow(id);

        validateCategoryOwnership(savedCategory);

        categoryRepository.delete(savedCategory);
    }

    private Category getCategoryOrThrow(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category not found"));
    }

    private void validateCategoryOwnership(Category category){
        User user = currentUserService.getCurrentUser();
        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            throw new CategoryAccessDeniedException("you are not allowed to modify this category");
        }
    }
}
