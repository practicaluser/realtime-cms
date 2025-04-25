package com.realtimecms.service;

import com.realtimecms.dto.AuthRequest;
import com.realtimecms.dto.AuthResponse;
import com.realtimecms.entity.User;
import com.realtimecms.exception.auth.EmailAlreadyExistsException;
import com.realtimecms.exception.auth.InvalidCredentialsException;
import com.realtimecms.repository.UserRepository;
import com.realtimecms.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

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

        User savedUser = new User();
        savedUser.setEmail(request.getEmail());
        savedUser.setPassword("encoded-password");
        savedUser.setRole("USER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("test4@example.com", "USER")).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtUtil, times(1)).generateToken("test4@example.com", "USER");
    }

    @Test
    void register_duplicateEmail_throwsException() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test4@example.com");
        request.setPassword("password123");
        request.setRole("USER");

        when(userRepository.existsByEmail("test4@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void register_nullRole_defaultsToUser() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test4@example.com");
        request.setPassword("password123");
        request.setRole(null);

        User savedUser = new User();
        savedUser.setEmail(request.getEmail());
        savedUser.setPassword("encoded-password");
        savedUser.setRole("USER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("test4@example.com", "USER")).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtUtil, times(1)).generateToken("test4@example.com", "USER");
    }

    @Test
    void login_success() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test4@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encoded-password");
        user.setRole("USER");

        when(userRepository.findByEmail("test4@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken("test4@example.com", "USER")).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(userRepository, times(1)).findByEmail("test4@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encoded-password");
        verify(jwtUtil, times(1)).generateToken("test4@example.com", "USER");
    }

    @Test
    void login_userNotFound_throwsException() {
        AuthRequest request = new AuthRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void login_invalidPassword_throwsException() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test4@example.com");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("test4@example.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encoded-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}