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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class CategoryServiceTest {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;
    private User user;

    public CategoryServiceTest() {
        this.categoryRepository = mock(CategoryRepository.class);
        this.currentUserService = mock(CurrentUserService.class);
        this.categoryService = new CategoryService(categoryRepository, currentUserService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setRole(Role.USER);
        when(currentUserService.getCurrentUser()).thenReturn(user);
    }

    @Test
    void shouldThrowExceptionWhenUserCreatesGlobalCategory() {
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest();
        categoryRequest.setName("food");
        categoryRequest.setGlobal(true);

        assertThrows(CannotCreateGlobalCategoryException.class, () -> categoryService.createCategory(categoryRequest));
    }

    @Test
    void shouldThrowExceptionWhenUserCreatesDuplicatePersonalCategory() {
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
        user.setRole(Role.ADMIN);

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
    void shouldSaveCategoryWhenUserCreatesNonDuplicateCategory() {
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
    void shouldSaveCategoryWhenAdminCreatesNonDuplicateGlobalCategory() {
        user.setRole(Role.ADMIN);

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

    @Test
    void shouldUpdateCategory() {
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("old name");
        savedCategory.setUser(user);


        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setName(" food ");

        when(categoryRepository.findById(savedCategory.getId()))
                .thenReturn(Optional.of(savedCategory));

        when(categoryRepository.existsByNameIgnoreCaseAndUser_IdAndIdNot(any(), any(), any()))
                .thenReturn(false);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        when(categoryRepository.save(any())).thenReturn(savedCategory);

        categoryService.updateCategory(savedCategory.getId(), updateCategoryRequest);

        verify(categoryRepository).save(captor.capture());
        Category updatedCategory = captor.getValue();

        assertThat(updatedCategory.getName()).isEqualTo("food");
        assertThat(updatedCategory.getId()).isEqualTo(1L);
        assertThat(updatedCategory.getUser().getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowCategoryNotFoundException() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(categoryId, null));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccessDeniedExceptionForGlobalCategory() {
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("old name");
        savedCategory.setUser(null);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(savedCategory));

        assertThrows(CategoryAccessDeniedException.class, () -> categoryService.updateCategory(1L, null));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccessDeniedException() {
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("old name");
        User anotherUser = new User(2L, "tester", "test@mail.com", "test@123", Role.USER);
        savedCategory.setUser(anotherUser);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(savedCategory));

        assertThrows(CategoryAccessDeniedException.class, () -> categoryService.updateCategory(1L, null));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowCategoryAlreadyExistsException() {
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("food");
        savedCategory.setUser(user);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(savedCategory));

        when(categoryRepository.existsByNameIgnoreCaseAndUser_IdAndIdNot("food", 1L, 1L)).thenReturn(true);

        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setName(" food ");
        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.updateCategory(1L, updateCategoryRequest));
        verify(categoryRepository, never()).save(any());
    }

}
