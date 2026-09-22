package com.ayush.expense_tracker_system.repository;

import com.ayush.expense_tracker_system.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}