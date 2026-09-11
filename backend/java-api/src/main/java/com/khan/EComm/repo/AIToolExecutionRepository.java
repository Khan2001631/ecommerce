package com.khan.EComm.repo;

import com.khan.EComm.model.AIToolExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIToolExecutionRepository
        extends JpaRepository<AIToolExecution, Long> {

    List<AIToolExecution> findByConversationIdOrderByCreatedAtAsc(
            Long conversationId
    );

    List<AIToolExecution> findByConversationIdAndToolNameOrderByCreatedAtDesc(
            Long conversationId,
            String toolName
    );
}