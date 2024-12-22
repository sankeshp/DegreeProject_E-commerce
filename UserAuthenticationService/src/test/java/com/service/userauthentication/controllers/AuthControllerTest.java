package com.service.userauthentication.controllers;

import com.service.userauthentication.dtos.SendNotificationMessageDTO;
import com.service.userauthentication.dtos.LoginCredentials;
import com.service.userauthentication.dtos.UserDTO;
import com.service.userauthentication.security.JWTUtil;
import com.service.userauthentication.configs.KafkaNotificationProducerClient;
import com.service.userauthentication.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private KafkaNotificationProducerClient kafkaNotificationProducerClient;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGetUserFromRedisCache() {
        String email = "test@example.com";
        UserDTO cachedUser = new UserDTO();
        cachedUser.setEmail(email);

        when(valueOperations.get("getUser_" + email)).thenReturn(cachedUser);

        ResponseEntity<UserDTO> response = authController.getUser(email);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(cachedUser, response.getBody());
        verify(userService, never()).getUserByEmail(anyString());
    }

    @Test
    void testGetUserNotFound() {
        String email = "nonexistent@example.com";

        when(valueOperations.get("getUser_" + email)).thenReturn(null);
        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<UserDTO> response = authController.getUser(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testRegisterUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");
        String encodedPassword = "encodedPassword";
        String jwtToken = "jwtToken";

        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn(encodedPassword);
        when(userService.registerUser(userDTO)).thenReturn(userDTO);
        when(jwtUtil.generateToken(userDTO.getEmail())).thenReturn(jwtToken);

        ResponseEntity<Map<String, Object>> response = authController.registerUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(kafkaNotificationProducerClient).publishNotiificationEvent(any(SendNotificationMessageDTO.class));
    }

    @Test
    void testLoginUser() {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password");
        String jwtToken = "jwtToken";

        when(jwtUtil.generateToken(credentials.getEmail())).thenReturn(jwtToken);

        Map<String, Object> response = authController.loginUser(credentials);

        assertEquals(Collections.singletonMap("jwt-token", jwtToken), response);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}

