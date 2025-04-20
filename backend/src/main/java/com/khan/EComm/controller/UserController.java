package com.khan.EComm.controller;

import com.khan.EComm.dto.LoginRequest;
import com.khan.EComm.model.User;
import com.khan.EComm.service.UserService;
import com.khan.EComm.utils.JwtUtil;
import com.khan.EComm.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String message = userService.registerUser(user);
        System.out.println(message);
        if (message.equals("User already exists with this email address")) {
            // Return a conflict status (409) when user already exists
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseMessage(message));
        }
        // Return a success response with a 201 status code when user is registered
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ResponseMessage(message));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User loggedInUser = userService.loginUser(request.getEmail(), request.getPassword());
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(loggedInUser.getEmail());
        Map<String,Object> response = new HashMap<>();
        response.put("token",token);
        response.put("user", Map.of(
                "id", loggedInUser.getId(),
                "name", loggedInUser.getName(),
                "email", loggedInUser.getEmail()
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

}
