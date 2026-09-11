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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
//@CrossOrigin("*")
public class UserController {

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

        String accessToken = jwtUtil.generateToken(loggedInUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken();
        
        userSessionService.createSession(loggedInUser, refreshToken);
        
        ResponseCookie accessCookie = jwtUtil.generateJwtCookie(accessToken);
        ResponseCookie refreshCookie = jwtUtil.generateRefreshJwtCookie(refreshToken);
        
        Map<String,Object> response = new HashMap<>();
        response.put("user", Map.of(
                "id", loggedInUser.getId(),
                "name", loggedInUser.getName(),
                "email", loggedInUser.getEmail()
        ));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
        if (refreshToken != null) {
            userSessionService.revokeSession(refreshToken);
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
        return userService.getAllUsers();
    }
}
