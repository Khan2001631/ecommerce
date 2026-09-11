package com.khan.EComm.repo;

import com.khan.EComm.model.AIConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AIConversationRepository extends JpaRepository<AIConversation, Long> {

    Optional<AIConversation> findBySessionId(String sessionId);
    List<AIConversation> findByUserIdOrderByUpdatedAtDesc(Long userId);
}