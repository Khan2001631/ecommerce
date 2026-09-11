package com.khan.EComm.repo;

import com.khan.EComm.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);
}
