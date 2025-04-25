package com.realtimecms.util;

import com.realtimecms.exception.auth.AuthException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey, @Value("${jwt.expiration-time}") long expirationTime) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            logger.error("JWT secret key cannot be null or empty");
            throw new IllegalArgumentException("JWT secret key cannot be null or empty");
        }
        if (expirationTime <= 0) {
            logger.error("JWT expiration time must be positive: {}", expirationTime);
            throw new IllegalArgumentException("JWT expiration time must be positive");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey.trim());
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
            logger.debug("JWT secret key initialized successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode JWT secret key: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid JWT secret key format", e);
        }
        this.expirationTime = expirationTime;
        logger.info("JwtUtil initialized with expiration time: {} ms", expirationTime);
    }

    public String generateToken(String email, String role) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Email cannot be null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (role == null || role.trim().isEmpty()) {
            logger.error("Role cannot be null or empty");
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        try {
            return Jwts.builder()
                    .setSubject(email)
                    .claim("role", role)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            logger.error("Failed to generate JWT for email: {}. Error: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }
}
