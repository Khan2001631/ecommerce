package com.khan.EComm.repo;

import com.khan.EComm.model.AIConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIConversationMessageRepository extends JpaRepository<AIConversationMessage, Long> {

    List<AIConversationMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    List<AIConversationMessage> findTop20ByConversationIdOrderByCreatedAtDesc(Long conversationId);
}