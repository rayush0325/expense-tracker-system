package com.ayush.expense_tracker_system.repository;

import com.ayush.expense_tracker_system.model.Category;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldGetAllCategoriesBelongingToUserOrWhoseUserIdIsNull(){
        User user1 = userRepository.save(new User("tester1@mail.com", "tester1", "tester1@123", Role.USER));
        User user2 = userRepository.save(new User("tester2@mail.com", "tester2", "tester2@123", Role.ADMIN));




        List<String> user1CategoryList = List.of("C1", "C3");
        List<String> user2CategoryList = List.of("C2", "C4");
        List<String> globalCategoryList = List.of("C5", "C7");



        user1CategoryList.forEach(category -> categoryRepository.save(new Category(category, user1)));
        user2CategoryList.forEach(category -> categoryRepository.save(new Category(category, user2)));
        globalCategoryList.forEach(category -> categoryRepository.save(new Category(category, null)));


        Set<String> user1SavedCategories = categoryRepository.findByUserIdOrUserIdIsNull(user1.getId())
                .stream()
                .map(category -> category.getName())
                        .collect(Collectors.toSet());

        user1CategoryList.forEach(category -> assertThat(user1SavedCategories.contains(category)).isTrue());
        globalCategoryList.forEach(category -> assertThat(user1SavedCategories.contains(category)).isTrue());
        user2CategoryList.forEach(category -> assertThat(user1SavedCategories.contains(category)).isFalse());


    }

    @Test
    void shouldGetTrueWhenDuplicateCategoryExists(){
        User user1 = userRepository.save(new User("tester1@mail.com", "tester1", "tester1@123", Role.USER));
        User user2 = userRepository.save(new User("tester2@mail.com", "tester2", "tester2@123", Role.ADMIN));


        List<String> user1CategoryList = List.of("food", "travel");
        List<String> user2CategoryList = List.of("C2", "C4");
        List<String> globalCategoryList = List.of("C5", "C7");

        user1CategoryList.forEach(category -> categoryRepository.save(new Category(category, user1)));
        user2CategoryList.forEach(category -> categoryRepository.save(new Category(category, user2)));
        globalCategoryList.forEach(category -> categoryRepository.save(new Category(category, null)));

        assertThat(categoryRepository.existsByNameIgnoreCaseAndUser_Id("FOOD", user1.getId())).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCaseAndUser_Id("C5", user1.getId())).isFalse();
    }
}
