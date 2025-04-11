package com.khan.EComm.service;

import com.khan.EComm.model.User;
import com.khan.EComm.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public void registerUser(User user) {
        userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        // Check if user is present in the database or not
        User user = userRepository.findByEmail(email);
        if(user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null; // invalid credentials
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
