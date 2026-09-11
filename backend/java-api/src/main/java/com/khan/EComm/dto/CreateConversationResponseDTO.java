package com.khan.EComm.dto;

public class CreateConversationResponseDTO {

    private Long conversationId;
    private String sessionId;

    public CreateConversationResponseDTO() {
    }

    public CreateConversationResponseDTO(Long conversationId, String sessionId) {
        this.conversationId = conversationId;
        this.sessionId = sessionId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}