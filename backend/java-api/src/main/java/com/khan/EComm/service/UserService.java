package com.khan.EComm.service;

import com.khan.EComm.events.EventPublisher;
import com.khan.EComm.events.UserRegisteredEvent;
import com.khan.EComm.model.User;
import com.khan.EComm.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
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
        // Check if user with the same email already exists
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        if (existingUser.isPresent()) {
            return "User already exists with this email address"; // Return a message indicating the user already exists
        }

        // If user does not exist, proceed with registration
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        // Publish UserRegisteredEvent
        eventPublisher.publish(
                new UserRegisteredEvent(user.getId(), user.getEmail())
        );
        return "User registered successfully"; // Success message
    }

    public User loginUser(String email, String password) {
        // Check if user is present in the database or not
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null; // invalid credentials
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + userId));
    }
}
