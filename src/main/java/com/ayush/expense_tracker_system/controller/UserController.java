package com.ayush.expense_tracker_system.controller;

import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.ErrorResponse;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.dto.response.ProfileResponse;
import com.ayush.expense_tracker_system.dto.response.RegisterResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.EmailAlreadyExistsException;
import com.ayush.expense_tracker_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.authenticateUser(loginRequest));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getProfile());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(Exception exception){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(){
        return ResponseEntity
                .status(HttpStatus.valueOf(401))
                .body(new ErrorResponse("invalid email or password"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmailOrPasswordException(Exception exception){
        return ResponseEntity
                .status(HttpStatus.valueOf(401))
                .body(new ErrorResponse("authentication failed"));
    }

}
