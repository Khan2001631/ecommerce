package com.khan.EComm.service;

import com.khan.EComm.events.EventPublisher;
import com.khan.EComm.events.UserRegisteredEvent;
import com.khan.EComm.model.User;
import com.khan.EComm.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.khan.EComm.exception.ResourceNotFoundException;
import com.khan.EComm.exception.UserAlreadyExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final EventPublisher eventPublisher;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }




    public String registerUser(User user) {
        log.info("Attempting to register new user with email: {}", user.getEmail());
        // Check if user with the same email already exists
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        if (existingUser.isPresent()) {
            log.warn("Registration failed: User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException("User already exists with this email address");
        }

        // If user does not exist, proceed with registration
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("Successfully registered user with email: {}", user.getEmail());
        // Publish UserRegisteredEvent
        eventPublisher.publish(
                new UserRegisteredEvent(user.getId(), user.getEmail())
        );
        return "User registered successfully"; // Success message
    }

    public User loginUser(String email, String password) {
        log.info("Attempting login for email: {}", email);
        // Check if user is present in the database or not
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            log.info("Login successful for user: {}", email);
            return user;
        }
        log.warn("Login failed for user: {} - Invalid email or password", email);
        return null; // invalid credentials
    }

    public List<User> getAllUsers() {
        log.info("Fetching all users from database");
        List<User> users = userRepository.findAll();
        log.info("Found {} users", users.size());
        return users;
    }

    public User getUserById(Long userId) {
        log.info("Fetching user by id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });
    }
}
