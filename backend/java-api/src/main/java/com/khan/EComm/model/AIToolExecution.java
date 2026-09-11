package com.khan.EComm.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_tool_execution")
public class AIToolExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "conversation_id",
            nullable = false
    )
    private AIConversation conversation;

    @Column(nullable = false)
    private String toolName;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String toolArguments;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String toolResponse;

    @Column(nullable = false)
    private Boolean success;

    private LocalDateTime createdAt;

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AIConversation getConversation() {
        return conversation;
    }

    public void setConversation(AIConversation conversation) {
        this.conversation = conversation;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getToolArguments() {
        return toolArguments;
    }

    public void setToolArguments(String toolArguments) {
        this.toolArguments = toolArguments;
    }

    public String getToolResponse() {
        return toolResponse;
    }

    public void setToolResponse(String toolResponse) {
        this.toolResponse = toolResponse;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}