package com.khan.EComm.utils;

import com.khan.EComm.model.UserSession;
import com.khan.EComm.service.CustomUserDetailsService;
import com.khan.EComm.service.UserSessionService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
            
        logger.info("==== JWT FILTER START ====");
        logger.info("Request URI: {}", request.getRequestURI());

        String jwt = jwtUtil.getJwtFromCookies(request);
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);

        String username = null;
        boolean jwtIsValid = false;

        if (jwt != null) {
            try {
                if (jwtUtil.validateToken(jwt)) {
                    username = jwtUtil.extractUsername(jwt);
                    jwtIsValid = true;
                    logger.info("Access token is valid for user: {}", username);
                }
            } catch (ExpiredJwtException e) {
                logger.info("Access token expired. Will attempt internal refresh.");
            } catch (Exception e) {
                logger.error("Access token validation failed: {}", e.getMessage());
            }
        }

        // Silent Internal Refresh Logic
        if (!jwtIsValid && refreshToken != null) {
            logger.info("Attempting silent internal refresh using refresh token...");
            Optional<UserSession> sessionOpt = userSessionService.validateSession(refreshToken);
            
            if (sessionOpt.isPresent()) {
                UserSession session = sessionOpt.get();
                username = session.getUser().getEmail();
                
                // Generate a new access token
                String newAccessToken = jwtUtil.generateToken(username);
                ResponseCookie accessCookie = jwtUtil.generateJwtCookie(newAccessToken);
                
                // Set it in the response so the browser updates its cookie jar
                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                
                jwtIsValid = true;
                logger.info("Internal token refresh successful for user: {}", username);
            } else {
                logger.warn("Refresh token was provided but is invalid, expired, or revoked.");
            }
        }

        // Set Security Context if valid
        if (jwtIsValid && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(token);
            logger.info("Authentication set in SecurityContext for user: {}", username);
        } else if (!jwtIsValid) {
            logger.warn("No valid authentication found for request.");
        }

        filterChain.doFilter(request, response);
    }
}

