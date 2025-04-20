package com.khan.EComm.config;

import com.khan.EComm.service.CustomUserDetailsService;
import com.khan.EComm.utils.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean   // Marks this method as a Spring bean so that Spring can manage it
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // Enables Cross-Origin Resource Sharing (CORS) with default settings
                .csrf(csrf -> csrf.disable()) // // Disables Cross-Site Request Forgery (CSRF) protection (common for stateless APIs)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/products/**", "/users/register", "/users/login").permitAll() // Allows public access to specified endpoints
                        .anyRequest().authenticated() // Requires authentication for all other endpoints
                )
                .userDetailsService(customUserDetailsService) // Configures the UserDetailsService to be used for authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configures session management to be stateless (no session creation)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Adds the JWT authentication filter before the UsernamePasswordAuthenticationFilter
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

