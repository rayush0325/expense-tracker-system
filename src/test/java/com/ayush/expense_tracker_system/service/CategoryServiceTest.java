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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class CategoryServiceTest {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public CategoryServiceTest() {
        this.categoryRepository = mock(CategoryRepository.class);
        this.categoryService = new CategoryService(categoryRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowExceptionWhenUserCreatesGlobalCategory() {
        User user = new User();
        user.setRole(Role.USER);
        setAuthenticatedUser(user);

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("food");
        categoryRequest.setGlobal(true);

        assertThrows(CannotCreateGlobalCategoryException.class, () -> categoryService.createCategory(categoryRequest));
    }

    @Test
    void shouldThrowExceptionWhenUserCreatesDuplicatePersonalCategory() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);
        setAuthenticatedUser(user);


        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("food");

        when(categoryRepository.existsByNameIgnoreCaseAndUser_Id(categoryRequest.getName(), user.getId()))
                .thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(categoryRequest));
        verify(categoryRepository, never()).existsByNameIgnoreCaseAndUserIsNull(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAdminCreatesDuplicateGlobalCategory() {
        User user = new User();
        user.setRole(Role.ADMIN);
        setAuthenticatedUser(user);


        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("food");
        categoryRequest.setGlobal(true);

        when(categoryRepository.existsByNameIgnoreCaseAndUserIsNull(categoryRequest.getName()))
                .thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(categoryRequest));
        verify(categoryRepository, never()).existsByNameIgnoreCaseAndUser_Id(any(), any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldSaveCategoryWhenUserCreatesNonDuplicateCategory(){
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);
        setAuthenticatedUser(user);

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("food");

        when(categoryRepository.existsByNameIgnoreCaseAndUser_Id(categoryRequest.getName(), user.getId()))
                .thenReturn(false);


        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName(categoryRequest.getName());
        savedCategory.setUser(user);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
                when(categoryRepository.save(any())).thenReturn(savedCategory);


        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
        verify(categoryRepository).save(captor.capture());

        Category newCategory = captor.getValue();
        assertThat(newCategory.getName()).isEqualTo(categoryRequest.getName().trim());
        assertThat(newCategory.getUser()).isEqualTo(user);

        assertThat(categoryResponse.getId()).isEqualTo(savedCategory.getId());
        assertThat(categoryResponse.getName()).isEqualTo(savedCategory.getName());
        assertThat(categoryResponse.getUserId()).isEqualTo(savedCategory.getUser().getId());

    }

    @Test
    void shouldSaveCategoryWhenAdminCreatesNonDuplicateGlobalCategory(){
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);
        setAuthenticatedUser(user);

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("food");
        categoryRequest.setGlobal(true);

        when(categoryRepository.existsByNameIgnoreCaseAndUserIsNull(categoryRequest.getName()))
                .thenReturn(false);

        Category category = new Category();
        category.setId(1L);
        category.setName(categoryRequest.getName());
        category.setUser(null);


        when(categoryRepository.save(any())).thenReturn(category);

        categoryService.createCategory(categoryRequest);
        verify(categoryRepository).save(any());
    }

    private void setAuthenticatedUser(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }
}
