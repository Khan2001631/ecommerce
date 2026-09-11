package com.khan.EComm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Separate configuration for PasswordEncoder to avoid a circular dependency:
 * SecurityConfig -> CustomUserDetailsService/UserService -> PasswordEncoder -> SecurityConfig
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
