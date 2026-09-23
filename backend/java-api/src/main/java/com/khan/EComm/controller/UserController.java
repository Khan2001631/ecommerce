package com.khan.EComm.controller;

import com.khan.EComm.dto.LoginRequest;
import com.khan.EComm.model.User;
import com.khan.EComm.service.UserService;
import com.khan.EComm.service.UserSessionService;
import com.khan.EComm.utils.JwtUtil;
import com.khan.EComm.utils.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
//@CrossOrigin("*")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserSessionService userSessionService;
    private final JwtUtil jwtUtil;

    // Constructor Injection:
    public UserController(UserService userService, UserSessionService userSessionService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        log.info("Received request to register user with email: {}", user.getEmail());
        String message = userService.registerUser(user);
        log.info("Registration result for email {}: {}", user.getEmail(), message);
        
        // Return a success response with a 201 status code when user is registered
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ResponseMessage(message));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());
        User loggedInUser = userService.loginUser(request.getEmail(), request.getPassword());
        if (loggedInUser == null) {
            log.warn("Login failed for email: {} - Invalid credentials", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        log.info("Login successful for email: {}. Generating tokens...", request.getEmail());

        String accessToken = jwtUtil.generateToken(loggedInUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken();
        
        userSessionService.createSession(loggedInUser, refreshToken);
        
        ResponseCookie accessCookie = jwtUtil.generateJwtCookie(accessToken);
        ResponseCookie refreshCookie = jwtUtil.generateRefreshJwtCookie(refreshToken);
        
        Map<String,Object> response = new HashMap<>();
        response.put("user", Map.of(
                "id", loggedInUser.getId(),
                "name", loggedInUser.getName(),
                "email", loggedInUser.getEmail(),
                "role", loggedInUser.getRole()
        ));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("Received logout request");
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
        if (refreshToken != null) {
            log.info("Revoking session associated with provided refresh token");
            userSessionService.revokeSession(refreshToken);
        } else {
            log.warn("Logout request received but no refresh token was found in cookies");
        }
        
        ResponseCookie cleanAccessCookie = jwtUtil.getCleanJwtCookie();
        ResponseCookie cleanRefreshCookie = jwtUtil.getCleanRefreshCookie();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefreshCookie.toString())
                .body(new ResponseMessage("You've been signed out!"));
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Received request to fetch all users");
        return userService.getAllUsers();
    }
}
