package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.dto.request.LoginRequest;
import com.ayush.expense_tracker_system.dto.request.RegisterRequest;
import com.ayush.expense_tracker_system.dto.response.LoginResponse;
import com.ayush.expense_tracker_system.dto.response.ProfileResponse;
import com.ayush.expense_tracker_system.dto.response.RegisterResponse;
import com.ayush.expense_tracker_system.dto.response.UserResponse;
import com.ayush.expense_tracker_system.exception.handler.custom.EmailAlreadyExistsException;
import com.ayush.expense_tracker_system.exception.handler.custom.UserNotFoundException;
import com.ayush.expense_tracker_system.model.CustomUserDetails;
import com.ayush.expense_tracker_system.model.Role;
import com.ayush.expense_tracker_system.model.User;
import com.ayush.expense_tracker_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public RegisterResponse registerUser(RegisterRequest registerRequest){
        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new EmailAlreadyExistsException("email already exists");
        }
        User newUser = new User(
                registerRequest.getEmail(),
                registerRequest.getName(),
                passwordEncoder.encode(registerRequest.getPassword()),
                Role.USER
        );

        User savedUser = userRepository.save(newUser);
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) throws AuthenticationException {

        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authenticated =  authenticationManager.authenticate(authenticationRequest);

        CustomUserDetails userDetails = (CustomUserDetails)authenticated.getPrincipal();

        User user = userDetails.getUser();
        String jwtToken =  jwtService.generateJwtToken(user);
        return new LoginResponse(jwtToken);
    }

    public ProfileResponse getProfile() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public List<UserResponse> getAllUsers(){
        Iterable<User> userList = userRepository.findAll();
        return StreamSupport.stream(userList.spliterator(), false)
                .map( user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .collect(Collectors.toList());
    }

    public void deleteUserById(Long id){
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException("user not found");
        }
        userRepository.deleteById(id);
    }
}
