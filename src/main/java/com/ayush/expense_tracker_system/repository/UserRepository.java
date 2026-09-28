package com.ayush.expense_tracker_system.repository;

import com.ayush.expense_tracker_system.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}