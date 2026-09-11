package com.khan.EComm.service;

import com.khan.EComm.model.User;
import com.khan.EComm.model.UserSession;
import com.khan.EComm.repo.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationMs;

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    @Transactional
    public UserSession createSession(User user, String refreshToken) {
        String tokenHash = hashToken(refreshToken);

        UserSession session = new UserSession();
        session.setUser(user);
        session.setRefreshTokenHash(tokenHash);
        session.setStatus("ACTIVE");
        session.setRefreshTokenExpiryTime(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));
        session.setCreatedDate(LocalDateTime.now());
        session.setLastActivityTimestamp(LocalDateTime.now());
        
        return userSessionRepository.save(session);
    }

    @Transactional
    public Optional<UserSession> validateSession(String refreshToken) {
        String tokenHash = hashToken(refreshToken);
        Optional<UserSession> sessionOpt = userSessionRepository.findByRefreshTokenHash(tokenHash);

        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            if ("ACTIVE".equals(session.getStatus()) && session.getRefreshTokenExpiryTime().isAfter(LocalDateTime.now())) {
                session.setLastActivityTimestamp(LocalDateTime.now());
                userSessionRepository.save(session);
                return Optional.of(session);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public void revokeSession(String refreshToken) {
        String tokenHash = hashToken(refreshToken);
        Optional<UserSession> sessionOpt = userSessionRepository.findByRefreshTokenHash(tokenHash);
        
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            session.setStatus("REVOKED");
            session.setUpdatedDate(LocalDateTime.now());
            userSessionRepository.save(session);
        }
    }
}
