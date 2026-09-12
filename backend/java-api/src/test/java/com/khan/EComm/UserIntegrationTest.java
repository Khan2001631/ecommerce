package com.khan.EComm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khan.EComm.dto.LoginRequest;
import com.khan.EComm.model.User;
import com.khan.EComm.repo.JpaUserRepository;
import com.khan.EComm.repo.UserRepository;
import com.khan.EComm.repo.UserSessionRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("User Domain Integration Tests")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        userSessionRepository.deleteAll();
        jpaUserRepository.deleteAll();
    }

    @Test
    @DisplayName("End-to-End User Flow: Register -> Login -> Access Protected Resource -> Logout")
    void testEndToEndUserFlow() throws Exception {
        // 1. REGISTER
        User registerUser = new User();
        registerUser.setName("Integration Test User");
        registerUser.setEmail("integration@example.com");
        registerUser.setPassword("password123");
        registerUser.setRole("USER"); // Required by DB constraint

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUser)))
                .andExpect(status().isCreated());

        // Verify user is in database
        User savedUser = userRepository.findByEmail("integration@example.com");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Integration Test User");
        // Password should be hashed
        assertThat(savedUser.getPassword()).isNotEqualTo("password123");

        // 2. LOGIN
        LoginRequest loginRequest = new LoginRequest("integration@example.com", "password123");

        MvcResult loginResult = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();

        Cookie accessTokenCookie = loginResult.getResponse().getCookie("accessToken");
        Cookie refreshTokenCookie = loginResult.getResponse().getCookie("refreshToken");

        assertThat(accessTokenCookie).isNotNull();
        assertThat(refreshTokenCookie).isNotNull();

        // Verify session is in database
        assertThat(userSessionRepository.findAll()).hasSize(1);
        assertThat(userSessionRepository.findAll().get(0).getStatus()).isEqualTo("ACTIVE");

        // 3. ACCESS PROTECTED RESOURCE (Get All Users)
        mockMvc.perform(get("/users")
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk());

        // 3.1 FAIL TO ACCESS WITHOUT COOKIE
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden()); // Or 401 Unauthorized depending on security config

        // 4. LOGOUT
        mockMvc.perform(post("/users/logout")
                        .cookie(accessTokenCookie, refreshTokenCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));

        // Verify session is revoked in database
        assertThat(userSessionRepository.findAll().get(0).getStatus()).isEqualTo("REVOKED");
    }
}
