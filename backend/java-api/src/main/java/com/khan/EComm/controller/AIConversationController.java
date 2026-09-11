package com.khan.EComm.controller;

import com.khan.EComm.dto.ConversationMessageResponseDTO;
import com.khan.EComm.dto.CreateConversationResponseDTO;
import com.khan.EComm.dto.SaveMessageRequestDTO;
import com.khan.EComm.dto.SaveToolExecutionRequestDTO;
import com.khan.EComm.model.AIConversation;
import com.khan.EComm.model.AIConversationMessage;
import com.khan.EComm.model.User;
import com.khan.EComm.service.AIConversationService;
import com.khan.EComm.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ai/conversations")
public class AIConversationController {

    private final AIConversationService conversationService;
    private final UserService userService;

    public AIConversationController(
            AIConversationService conversationService,
            UserService userService
    ) {
        this.conversationService = conversationService;
        this.userService = userService;
    }

    /**
     * Creates a new AI conversation for the specified user.
     *
     * @param userId ID of the user creating the conversation
     * @return Created conversation details including ID and session ID
     */
    @PostMapping
    public ResponseEntity<CreateConversationResponseDTO> createConversation(
            @RequestParam Long userId
    ) {

        User user = userService.getUserById(userId);

        AIConversation conversation =
                conversationService.createConversation(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new CreateConversationResponseDTO(
                                conversation.getId(),
                                conversation.getSessionId()
                        )
                );
    }

    /**
     * Retrieves a conversation using its session ID.
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<CreateConversationResponseDTO> getConversationBySessionId(
            @PathVariable String sessionId
    ) {

        Optional<AIConversation> conversation =
                conversationService.getConversationBySessionId(sessionId);

        if (conversation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new CreateConversationResponseDTO(
                        conversation.get().getId(),
                        conversation.get().getSessionId()
                )
        );
    }

    /**
     * Saves a message in an existing conversation.
     */
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<Void> saveMessage(
            @PathVariable Long conversationId,
            @RequestBody SaveMessageRequestDTO request
    ) {

        Optional<AIConversation> conversation =
                conversationService.getConversation(conversationId);

        if (conversation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        conversationService.saveMessage(
                conversation.get(),
                request.getRole(),
                request.getContent()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Returns all messages belonging to a conversation.
     */
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<ConversationMessageResponseDTO>> getMessages(
            @PathVariable Long conversationId
    ) {

        List<AIConversationMessage> messages =
                conversationService.getConversationHistory(conversationId);

        List<ConversationMessageResponseDTO> response =
                messages.stream()
                        .map(message ->
                                new ConversationMessageResponseDTO(
                                        message.getId(),
                                        message.getRole(),
                                        message.getContent(),
                                        message.getCreatedAt()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Stores a tool execution record for a conversation.
     */
    @PostMapping("/{conversationId}/tools")
    public ResponseEntity<Void> saveToolExecution(
            @PathVariable Long conversationId,
            @RequestBody SaveToolExecutionRequestDTO request
    ) {

        Optional<AIConversation> conversation =
                conversationService.getConversation(conversationId);

        if (conversation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        conversationService.saveToolExecution(
                conversation.get(),
                request.getToolName(),
                request.getToolArguments(),
                request.getToolResponse(),
                request.getSuccess()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieves a conversation by its database ID.
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<CreateConversationResponseDTO> getConversation(
            @PathVariable Long conversationId
    ) {

        Optional<AIConversation> conversation =
                conversationService.getConversation(conversationId);

        if (conversation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new CreateConversationResponseDTO(
                        conversation.get().getId(),
                        conversation.get().getSessionId()
                )
        );
    }
}