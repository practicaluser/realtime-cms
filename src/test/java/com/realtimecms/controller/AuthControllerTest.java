package com.realtimecms.controller;

import com.realtimecms.dto.AuthRequest;
import com.realtimecms.dto.AuthResponse;
import com.realtimecms.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test4@example.com");
        request.setPassword("password123");
        request.setRole("USER");
        AuthResponse response = new AuthResponse("mocked-jwt-token");
        when(authService.register(request)).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.register(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(authService, times(1)).register(request);
    }

    @Test
    void login_success() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test4@example.com");
        request.setPassword("password123");
        AuthResponse response = new AuthResponse("mocked-jwt-token");
        when(authService.login(request)).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(authService, times(1)).login(request);
    }

    @Test
    void register_invalidInput_throwsException() {
        AuthRequest request = new AuthRequest();
        request.setEmail("");
        request.setPassword("password123");
        request.setRole("USER");
        when(authService.register(request)).thenThrow(new IllegalArgumentException("Email is required"));

        assertThrows(IllegalArgumentException.class, () -> authController.register(request));
        verify(authService, times(1)).register(request);
    }

    @Test
    void login_invalidInput_throwsException() {
        AuthRequest request = new AuthRequest();
        request.setEmail("");
        request.setPassword("password123");
        when(authService.login(request)).thenThrow(new IllegalArgumentException("Email is required"));

        assertThrows(IllegalArgumentException.class, () -> authController.login(request));
        verify(authService, times(1)).login(request);
    }
}