package com.khan.EComm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class DeploymentCheckController {

    @GetMapping("/version")
    public Map<String, String> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "CI/CD Pipeline works! Latest code is deployed.");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
}
