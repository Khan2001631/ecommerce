package com.khan.EComm.service;

import com.khan.EComm.model.AIConversation;
import com.khan.EComm.model.User;
import com.khan.EComm.model.AIConversationMessage;
import com.khan.EComm.model.AIToolExecution;
import com.khan.EComm.repo.AIConversationMessageRepository;
import com.khan.EComm.repo.AIConversationRepository;
import com.khan.EComm.repo.AIToolExecutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AIConversationService {

    private final AIConversationRepository conversationRepository;
    private final AIConversationMessageRepository messageRepository;
    private final AIToolExecutionRepository toolExecutionRepository;

    private static final List<String> ALLOWED_ROLES =
            List.of("user", "assistant", "tool", "system");

    public AIConversationService(
            AIConversationRepository conversationRepository,
            AIConversationMessageRepository messageRepository,
            AIToolExecutionRepository toolExecutionRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.toolExecutionRepository = toolExecutionRepository;
    }

    public AIConversation createConversation(User user) {

        AIConversation conversation = new AIConversation();

        conversation.setUser(user);
        conversation.setSessionId(UUID.randomUUID().toString());
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());

        return conversationRepository.save(conversation);
    }

    public Optional<AIConversation> getConversationBySessionId(
            String sessionId
    ) {
        return conversationRepository.findBySessionId(sessionId);
    }

    public AIConversationMessage saveMessage(
            AIConversation conversation,
            String role,
            String content
    ) {

        AIConversationMessage message = new AIConversationMessage();

        if (!ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return messageRepository.save(message);
    }

    public List<AIConversationMessage> getConversationHistory(
            Long conversationId
    ) {
        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    public AIToolExecution saveToolExecution(
            AIConversation conversation,
            String toolName,
            String toolArguments,
            String toolResponse,
            boolean success
    ) {

        AIToolExecution execution = new AIToolExecution();

        execution.setConversation(conversation);
        execution.setToolName(toolName);
        execution.setToolArguments(toolArguments);
        execution.setToolResponse(toolResponse);
        execution.setSuccess(success);
        execution.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return toolExecutionRepository.save(execution);
    }

    public Optional<AIConversation> getConversation(Long conversationId) {
        return conversationRepository.findById(conversationId);
    }

    public List<AIConversation> getUserConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }
}