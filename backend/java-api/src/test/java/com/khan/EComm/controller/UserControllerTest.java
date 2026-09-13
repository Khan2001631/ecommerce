package com.khan.EComm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khan.EComm.dto.LoginRequest;
import com.khan.EComm.model.User;
import com.khan.EComm.service.UserService;
import com.khan.EComm.service.UserSessionService;
import com.khan.EComm.utils.JwtUtil;
import com.khan.EComm.exception.GlobalExceptionHandler;
import com.khan.EComm.exception.UserAlreadyExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /users/register Tests")
    class RegisterEndpointTests {

        @Test
        @DisplayName("Should return 201 CREATED when registration is successful")
        void register_Success_Returns201() throws Exception {
            User user = new User();
            user.setName("Alice");
            user.setEmail("alice@example.com");
            user.setPassword("password123");
            user.setRole("USER");

            when(userService.registerUser(any(User.class))).thenReturn("User registered successfully");

            mockMvc.perform(post("/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message", is("User registered successfully")));

            verify(userService).registerUser(any(User.class));
        }

        @Test
        @DisplayName("Should return 409 CONFLICT when email already exists")
        void register_UserAlreadyExists_Returns409() throws Exception {
            User user = new User();
            user.setName("Alice");
            user.setEmail("alice@example.com");
            user.setPassword("password123");
            user.setRole("USER");

            when(userService.registerUser(any(User.class)))
                    .thenThrow(new UserAlreadyExistsException("User already exists with this email address"));

            mockMvc.perform(post("/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", is("User already exists with this email address")));

            verify(userService).registerUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("POST /users/login Tests")
    class LoginEndpointTests {

        @Test
        @DisplayName("Should return 200 OK with auth cookies and user info when login is valid")
        void login_Success_Returns200WithCookiesAndUser() throws Exception {
            LoginRequest loginRequest = new LoginRequest("alice@example.com", "password123");

            User loggedInUser = new User();
            loggedInUser.setId(10L);
            loggedInUser.setName("Alice");
            loggedInUser.setEmail("alice@example.com");

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", "mock-access-token")
                    .path("/")
                    .httpOnly(true)
                    .build();
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "mock-refresh-token")
                    .path("/")
                    .httpOnly(true)
                    .build();

            when(userService.loginUser("alice@example.com", "password123")).thenReturn(loggedInUser);
            when(jwtUtil.generateToken("alice@example.com")).thenReturn("mock-access-token");
            when(jwtUtil.generateRefreshToken()).thenReturn("mock-refresh-token");
            when(jwtUtil.generateJwtCookie("mock-access-token")).thenReturn(accessCookie);
            when(jwtUtil.generateRefreshJwtCookie("mock-refresh-token")).thenReturn(refreshCookie);

            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(jsonPath("$.user.id", is(10)))
                    .andExpect(jsonPath("$.user.name", is("Alice")))
                    .andExpect(jsonPath("$.user.email", is("alice@example.com")));

            verify(userService).loginUser("alice@example.com", "password123");
            verify(userSessionService).createSession(loggedInUser, "mock-refresh-token");
        }

        @Test
        @DisplayName("Should return 401 UNAUTHORIZED when login credentials are invalid")
        void login_InvalidCredentials_Returns401() throws Exception {
            LoginRequest loginRequest = new LoginRequest("alice@example.com", "wrongPassword");

            when(userService.loginUser("alice@example.com", "wrongPassword")).thenReturn(null);

            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Invalid credentials"));

            verify(userService).loginUser("alice@example.com", "wrongPassword");
            verifyNoInteractions(jwtUtil);
            verifyNoInteractions(userSessionService);
        }
    }

    @Nested
    @DisplayName("POST /users/logout Tests")
    class LogoutEndpointTests {

        @Test
        @DisplayName("Should revoke session and clear cookies when refreshToken is present")
        void logout_WithRefreshToken_RevokesSessionAndClearsCookies() throws Exception {
            ResponseCookie cleanAccessCookie = ResponseCookie.from("accessToken", "").maxAge(0).build();
            ResponseCookie cleanRefreshCookie = ResponseCookie.from("refreshToken", "").maxAge(0).build();

            when(jwtUtil.getRefreshTokenFromCookies(any(HttpServletRequest.class))).thenReturn("active-refresh-token");
            when(jwtUtil.getCleanJwtCookie()).thenReturn(cleanAccessCookie);
            when(jwtUtil.getCleanRefreshCookie()).thenReturn(cleanRefreshCookie);

            mockMvc.perform(post("/users/logout")
                            .cookie(new Cookie("refreshToken", "active-refresh-token")))
                    .andExpect(status().isOk())
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(jsonPath("$.message", is("You've been signed out!")));

            verify(jwtUtil).getRefreshTokenFromCookies(any(HttpServletRequest.class));
            verify(userSessionService).revokeSession("active-refresh-token");
            verify(jwtUtil).getCleanJwtCookie();
            verify(jwtUtil).getCleanRefreshCookie();
        }

        @Test
        @DisplayName("Should clear cookies and return 200 even when refreshToken cookie is absent")
        void logout_WithoutRefreshToken_ClearsCookiesWithoutRevoke() throws Exception {
            ResponseCookie cleanAccessCookie = ResponseCookie.from("accessToken", "").maxAge(0).build();
            ResponseCookie cleanRefreshCookie = ResponseCookie.from("refreshToken", "").maxAge(0).build();

            when(jwtUtil.getRefreshTokenFromCookies(any(HttpServletRequest.class))).thenReturn(null);
            when(jwtUtil.getCleanJwtCookie()).thenReturn(cleanAccessCookie);
            when(jwtUtil.getCleanRefreshCookie()).thenReturn(cleanRefreshCookie);

            mockMvc.perform(post("/users/logout"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                    .andExpect(jsonPath("$.message", is("You've been signed out!")));

            verify(jwtUtil).getRefreshTokenFromCookies(any(HttpServletRequest.class));
            verify(userSessionService, never()).revokeSession(any());
            verify(jwtUtil).getCleanJwtCookie();
            verify(jwtUtil).getCleanRefreshCookie();
        }
    }

    @Nested
    @DisplayName("GET /users Tests")
    class GetAllUsersEndpointTests {

        @Test
        @DisplayName("Should return 200 OK with list of all users")
        void getAllUsers_Returns200WithList() throws Exception {
            User user1 = new User();
            user1.setId(1L);
            user1.setName("User One");
            user1.setEmail("user1@example.com");

            User user2 = new User();
            user2.setId(2L);
            user2.setName("User Two");
            user2.setEmail("user2@example.com");

            when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].email", is("user1@example.com")))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].email", is("user2@example.com")));

            verify(userService).getAllUsers();
        }
    }
}
