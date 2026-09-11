package com.khan.EComm.dto;

import java.time.LocalDateTime;

public class ConversationMessageResponseDTO {

    private Long id;
    private String role;
    private String content;
    private LocalDateTime createdAt;

    public ConversationMessageResponseDTO() {
    }

    public ConversationMessageResponseDTO(
            Long id,
            String role,
            String content,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}