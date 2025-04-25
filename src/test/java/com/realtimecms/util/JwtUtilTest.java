package com.realtimecms.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private JwtUtil jwtUtil;
    private final String secretKey = Base64.getEncoder().encodeToString("your-very-secure-key-32bytes-long-at-least".getBytes());
    private final long expirationTime = 3600000;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secretKey, expirationTime);
    }

    @Test
    void generateToken_success() {
        String token = jwtUtil.generateToken("test4@example.com", "USER");
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
        assertEquals("test4@example.com", claims.getSubject());
        assertEquals("USER", claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateToken_emptyKey_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil("", expirationTime));
    }

    @Test
    void generateToken_invalidExpiration_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil(secretKey, -1000));
    }

    @Test
    void generateToken_emptyEmail_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken("", "USER"));
    }

    @Test
    void generateToken_nullEmail_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken(null, "USER"));
    }

    @Test
    void generateToken_emptyRole_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken("test4@example.com", ""));
    }

    @Test
    void generateToken_nullRole_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.generateToken("test4@example.com", null));
    }
}