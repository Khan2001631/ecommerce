package com.khan.EComm.service;

import com.khan.EComm.model.User;
import com.khan.EComm.model.UserSession;
import com.khan.EComm.repo.UserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserSessionService Unit Tests")
class UserSessionServiceTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @InjectMocks
    private UserSessionService userSessionService;

    private static final long REFRESH_EXPIRATION_MS = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userSessionService, "refreshExpirationMs", REFRESH_EXPIRATION_MS);
    }

    @Nested
    @DisplayName("hashToken Tests")
    class HashTokenTests {

        @Test
        @DisplayName("Should hash token deterministically using SHA-256 and Base64")
        void shouldHashTokenConsistently() {
            String token = "sample-refresh-token-uuid-1234";

            String hash1 = userSessionService.hashToken(token);
            String hash2 = userSessionService.hashToken(token);

            assertThat(hash1).isNotEmpty();
            assertThat(hash1).isEqualTo(hash2);
            assertThat(hash1).isNotEqualTo(token);
        }

        @Test
        @DisplayName("Should generate different hashes for different tokens")
        void shouldProduceDistinctHashesForDistinctTokens() {
            String token1 = "token-one";
            String token2 = "token-two";

            String hash1 = userSessionService.hashToken(token1);
            String hash2 = userSessionService.hashToken(token2);

            assertThat(hash1).isNotEqualTo(hash2);
        }
    }

    @Nested
    @DisplayName("createSession Tests")
    class CreateSessionTests {

        @Test
        @DisplayName("Should create and save active session with calculated expiry time")
        void shouldCreateAndSaveActiveSession() {
            User user = new User();
            user.setId(1L);
            user.setEmail("user@example.com");

            String refreshToken = "my-secret-refresh-token";
            String expectedHash = userSessionService.hashToken(refreshToken);

            when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserSession result = userSessionService.createSession(user, refreshToken);

            assertThat(result).isNotNull();
            assertThat(result.getUser()).isEqualTo(user);
            assertThat(result.getRefreshTokenHash()).isEqualTo(expectedHash);
            assertThat(result.getStatus()).isEqualTo("ACTIVE");
            assertThat(result.getRefreshTokenExpiryTime()).isAfter(LocalDateTime.now());
            assertThat(result.getCreatedDate()).isNotNull();
            assertThat(result.getLastActivityTimestamp()).isNotNull();

            verify(userSessionRepository, times(1)).save(any(UserSession.class));
        }
    }

    @Nested
    @DisplayName("validateSession Tests")
    class ValidateSessionTests {

        @Test
        @DisplayName("Should validate active unexpired session and update last activity timestamp")
        void shouldValidateActiveUnexpiredSession() {
            String refreshToken = "valid-refresh-token";
            String tokenHash = userSessionService.hashToken(refreshToken);

            UserSession session = new UserSession();
            session.setUserSessionId(100L);
            session.setRefreshTokenHash(tokenHash);
            session.setStatus("ACTIVE");
            session.setRefreshTokenExpiryTime(LocalDateTime.now().plusDays(3));
            LocalDateTime beforeValidation = LocalDateTime.now().minusMinutes(5);
            session.setLastActivityTimestamp(beforeValidation);
            
            User user = new User();
            user.setEmail("test@example.com");
            session.setUser(user);

            when(userSessionRepository.findByRefreshTokenHash(tokenHash)).thenReturn(Optional.of(session));
            when(userSessionRepository.save(any(UserSession.class))).thenReturn(session);

            Optional<UserSession> validated = userSessionService.validateSession(refreshToken);

            assertThat(validated).isPresent();
            assertThat(validated.get().getStatus()).isEqualTo("ACTIVE");
            assertThat(validated.get().getLastActivityTimestamp()).isAfterOrEqualTo(beforeValidation);
            verify(userSessionRepository, times(1)).save(session);
        }

        @Test
        @DisplayName("Should return empty when session is expired")
        void shouldReturnEmptyWhenSessionExpired() {
            String refreshToken = "expired-token";
            String tokenHash = userSessionService.hashToken(refreshToken);

            UserSession session = new UserSession();
            session.setRefreshTokenHash(tokenHash);
            session.setStatus("ACTIVE");
            session.setRefreshTokenExpiryTime(LocalDateTime.now().minusDays(1)); // Expired

            when(userSessionRepository.findByRefreshTokenHash(tokenHash)).thenReturn(Optional.of(session));

            Optional<UserSession> validated = userSessionService.validateSession(refreshToken);

            assertThat(validated).isEmpty();
            verify(userSessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return empty when session is revoked")
        void shouldReturnEmptyWhenSessionRevoked() {
            String refreshToken = "revoked-token";
            String tokenHash = userSessionService.hashToken(refreshToken);

            UserSession session = new UserSession();
            session.setRefreshTokenHash(tokenHash);
            session.setStatus("REVOKED");
            session.setRefreshTokenExpiryTime(LocalDateTime.now().plusDays(5));

            when(userSessionRepository.findByRefreshTokenHash(tokenHash)).thenReturn(Optional.of(session));

            Optional<UserSession> validated = userSessionService.validateSession(refreshToken);

            assertThat(validated).isEmpty();
            verify(userSessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return empty when session not found in repository")
        void shouldReturnEmptyWhenSessionNotFound() {
            String refreshToken = "unknown-token";
            String tokenHash = userSessionService.hashToken(refreshToken);

            when(userSessionRepository.findByRefreshTokenHash(tokenHash)).thenReturn(Optional.empty());

            Optional<UserSession> validated = userSessionService.validateSession(refreshToken);

            assertThat(validated).isEmpty();
            verify(userSessionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("revokeSession Tests")
    class RevokeSessionTests {

        @Test
        @DisplayName("Should revoke existing session and update timestamp")
        void shouldRevokeExistingSession() {
            String refreshToken = "token-to-revoke";
            String tokenHash = userSessionService.hashToken(refreshToken);

            UserSession session = new UserSession();
            session.setUserSessionId(50L);
            session.setRefreshTokenHash(tokenHash);
            session.setStatus("ACTIVE");
            
            User user = new User();
            user.setEmail("test@example.com");
            session.setUser(user);

            when(userSessionRepository.findByRefreshTokenHash(tokenHash)).thenReturn(Optional.of(session));

            userSessionService.revokeSession(refreshToken);

            ArgumentCaptor<UserSession> captor = ArgumentCaptor.forClass(UserSession.class);
            verify(userSessionRepository, times(1)).save(captor.capture());

            UserSession saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo("REVOKED");
            assertThat(saved.getUpdatedDate()).isNotNull();
        }

        @Test
        @DisplayName("Should do nothing when revoking non-existent session")
        void shouldDoNothingWhenRevokingNonExistentSession() {
            String refreshToken = "unknown-token";
            String tokenHash = userSessionService.hashToken(refreshToken);

            when(userSessionRepository.findByRefreshTokenHash(tokenHash)).thenReturn(Optional.empty());

            userSessionService.revokeSession(refreshToken);

            verify(userSessionRepository, never()).save(any());
        }
    }
}
