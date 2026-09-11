package com.khan.EComm.ratelimit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

//Why OncePerRequestFilter?
//Guarantees the filter runs once per request
//Prevents double execution during forwards/dispatches
/**
 * Rate limiting filter for login and register endpoints.
 * - IP based throttling (login + register)
 * - Per-email throttling on FAILED login attempts
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitBucketManager loginBucketManager;
    private final RateLimitBucketManager registerBucketManager;
    private final RateLimitBucketManager loginFailureBucketManager;
    private final TokenBucketManager apiBucketManager;


    private final ObjectMapper objectMapper = new ObjectMapper();


    public RateLimitFilter() {
        // 5 requests per minute
        int capacity = 5;
        double leakRatePerSecond = 5.0 / 60.0;

        this.loginBucketManager = new RateLimitBucketManager(capacity, leakRatePerSecond);
        this.registerBucketManager = new RateLimitBucketManager(capacity, leakRatePerSecond);
        // Per-email failed login limit: 5 failures per 10 minutes
        this.loginFailureBucketManager = new RateLimitBucketManager(5, 5.0 / 600.0);
        this.apiBucketManager = new TokenBucketManager(20,1.0);

    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Allow preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

    /* =====================
       LOGIN ENDPOINT
       ===================== */
        if (path.equals("/api/users/login")) {

            String clientIp = getClientIp(request);

            // 1️⃣ IP-based rate limiting
            if (!loginBucketManager.allowRequest(clientIp)) {
                reject(response, "Too many login attempts from this IP. Please try again later.");
                return;
            }

            // Wrap response to inspect status after controller execution
            ContentCachingResponseWrapper wrappedResponse =
                    new ContentCachingResponseWrapper(response);

            filterChain.doFilter(request, wrappedResponse);

            // 2️⃣ Per-email throttling on FAILED login
            if (wrappedResponse.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
                String email = extractEmail(request);

                // HARD BLOCK after too many failures
                if (!loginFailureBucketManager.allowRequest(email)) {
                    reject(wrappedResponse,
                            "Account temporarily locked due to repeated failed login attempts.");
                    wrappedResponse.copyBodyToResponse();
                    return;
                }
                System.out.println("Failed login recorded for email: " + email);
            }


            wrappedResponse.copyBodyToResponse();
            return;
        }

    /* =====================
       REGISTER ENDPOINT
       ===================== */
        if (path.equals("/api/users/register")) {

            String clientIp = getClientIp(request);

            if (!registerBucketManager.allowRequest(clientIp)) {
                reject(response, "Too many registration attempts. Please try again later.");
                return;
            }
        }

        // Token bucket for all other APIs (excluding login/register)
        if (!path.startsWith("/api/users/login") &&
                !path.startsWith("/api/users/register")) {

            String clientIp = getClientIp(request);

            if (!apiBucketManager.allowRequest(clientIp)) {
                reject(response, "Too many requests. Please slow down.");
                return;
            }
        }


        // All other requests
        filterChain.doFilter(request, response);
    }


    // Figures out the real IP address of the client making the HTTP request
    private String getClientIp(HttpServletRequest request) {
        // X-Forwarded-For is an HTTP header which contains the original client IP address
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write(
                "{ \"error\": \"RATE_LIMITED\", \"message\": \"" + message + "\" }"
        );
    }

    private String extractEmail(HttpServletRequest request) {
        try {
            JsonNode root = objectMapper.readTree(request.getInputStream());
            JsonNode emailNode = root.get("email");
            return emailNode != null ? emailNode.asText() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }
}

/*
    LeakyBucket → decides allow / deny
    RateLimitBucketManager → decides which bucket to use

    But neither of them ever sees an HTTP request.
        RateLimitFilter is the component that actually intercepts incoming HTTP requests and applies rate limiting before
        your controllers are reached.

    We are using filters as it run before controllers.

    User -> HTTP Request -> RateLimitFilter(Checks path (/login or /register)) -> RateLimitBucketManager(Finds/create bucket for client IP)
    -> Leaky Bucket(Decides allow or deny based on capacity & leak rate) (if allowed -> controller), (if rejected -> Denied (429))
 */
