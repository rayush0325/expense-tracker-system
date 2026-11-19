package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.model.CustomUserDetails;
import com.ayush.expense_tracker_system.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)){
            throw new AccessDeniedException("user not authenticated");
        }

        return userDetails.getUser();
    }
}
