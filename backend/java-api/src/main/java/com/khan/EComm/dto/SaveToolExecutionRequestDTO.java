package com.khan.EComm.dto;


public class SaveToolExecutionRequestDTO {

    private String toolName;

    private String toolArguments;

    private String toolResponse;

    private Boolean success;

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
}