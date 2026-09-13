package com.khan.EComm.service;

import com.khan.EComm.events.EventPublisher;
import com.khan.EComm.events.UserRegisteredEvent;
import com.khan.EComm.model.User;
import com.khan.EComm.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import com.khan.EComm.exception.ResourceNotFoundException;
import com.khan.EComm.exception.UserAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john@example.com");
        sampleUser.setPassword("plainPassword");
        sampleUser.setRole("USER");
    }

    @Nested
    @DisplayName("registerUser Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("Should successfully register user, encode password, save to repository, and publish event")
        void registerUser_Success() {
            // Arrange
            when(userRepository.findByEmail("john@example.com")).thenReturn(null);
            when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            // Act
            String result = userService.registerUser(sampleUser);

            // Assert
            assertThat(result).isEqualTo("User registered successfully");
            assertThat(sampleUser.getPassword()).isEqualTo("encodedPassword");

            verify(userRepository).findByEmail("john@example.com");
            verify(passwordEncoder).encode("plainPassword");
            verify(userRepository).save(sampleUser);

            ArgumentCaptor<UserRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
            verify(eventPublisher).publish(eventCaptor.capture());
            UserRegisteredEvent publishedEvent = eventCaptor.getValue();
            assertThat(publishedEvent.getEventType()).isEqualTo("USER_REGISTERED");

            UserRegisteredEvent.Data eventData = (UserRegisteredEvent.Data) publishedEvent.getData();
            assertThat(eventData.getUserId()).isEqualTo(1L);
            assertThat(eventData.getEmail()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Should return conflict message and not save or publish event when user email already exists")
        void registerUser_UserAlreadyExists() {
            // Arrange
            User existingUser = new User();
            existingUser.setId(2L);
            existingUser.setEmail("john@example.com");

            when(userRepository.findByEmail("john@example.com")).thenReturn(existingUser);

            // Act & Assert
            assertThatThrownBy(() -> userService.registerUser(sampleUser))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("User already exists with this email address");

            verify(userRepository).findByEmail("john@example.com");
            verifyNoInteractions(passwordEncoder);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(eventPublisher);
        }
    }

    @Nested
    @DisplayName("loginUser Tests")
    class LoginUserTests {

        @Test
        @DisplayName("Should return user when credentials are valid")
        void loginUser_Success() {
            // Arrange
            sampleUser.setPassword("encodedPassword");
            when(userRepository.findByEmail("john@example.com")).thenReturn(sampleUser);
            when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

            // Act
            User loggedInUser = userService.loginUser("john@example.com", "rawPassword");

            // Assert
            assertThat(loggedInUser).isNotNull();
            assertThat(loggedInUser.getEmail()).isEqualTo("john@example.com");
            verify(userRepository).findByEmail("john@example.com");
            verify(passwordEncoder).matches("rawPassword", "encodedPassword");
        }

        @Test
        @DisplayName("Should return null when user is not found")
        void loginUser_UserNotFound() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

            // Act
            User loggedInUser = userService.loginUser("nonexistent@example.com", "rawPassword");

            // Assert
            assertThat(loggedInUser).isNull();
            verify(userRepository).findByEmail("nonexistent@example.com");
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("Should return null when password does not match")
        void loginUser_WrongPassword() {
            // Arrange
            sampleUser.setPassword("encodedPassword");
            when(userRepository.findByEmail("john@example.com")).thenReturn(sampleUser);
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            // Act
            User loggedInUser = userService.loginUser("john@example.com", "wrongPassword");

            // Assert
            assertThat(loggedInUser).isNull();
            verify(userRepository).findByEmail("john@example.com");
            verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        }
    }

    @Nested
    @DisplayName("getAllUsers Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users from repository")
        void getAllUsers_Success() {
            // Arrange
            User secondUser = new User();
            secondUser.setId(2L);
            secondUser.setName("Jane Doe");
            secondUser.setEmail("jane@example.com");

            when(userRepository.findAll()).thenReturn(List.of(sampleUser, secondUser));

            // Act
            List<User> users = userService.getAllUsers();

            // Assert
            assertThat(users).hasSize(2).containsExactly(sampleUser, secondUser);
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getUserById Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when id exists")
        void getUserById_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

            // Act
            User foundUser = userService.getUserById(1L);

            // Assert
            assertThat(foundUser).isNotNull();
            assertThat(foundUser.getId()).isEqualTo(1L);
            assertThat(foundUser.getEmail()).isEqualTo("john@example.com");
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw RuntimeException when user id is not found")
        void getUserById_NotFound_ThrowsException() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found with id: 999");

            verify(userRepository).findById(999L);
        }
    }
}
