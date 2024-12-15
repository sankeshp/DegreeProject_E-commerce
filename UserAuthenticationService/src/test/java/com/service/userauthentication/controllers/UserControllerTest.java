package com.service.userauthentication.controllers;

import com.service.userauthentication.dtos.UserDTO;
import com.service.userauthentication.dtos.UserResponse;
import com.service.userauthentication.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers_ShouldReturnUserResponse() {
        UserResponse userResponse = new UserResponse();
        userResponse.setContent(Arrays.asList(new UserDTO(), new UserDTO()));
        userResponse.setPageNumber(1);
        userResponse.setPageSize(10);

        when(userService.getAllUsers(1, 10, "id", "asc")).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getUsers(1, 10, "id", "asc");

        assertNotNull(response);
        assertEquals(302, response.getStatusCodeValue()); // HttpStatus.FOUND
        assertEquals(userResponse, response.getBody());
        verify(userService, times(1)).getAllUsers(1, 10, "id", "asc");
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);

        when(userService.updateUser(userId, userDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateUser(userDTO, userId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue()); // HttpStatus.OK
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).updateUser(userId, userDTO);
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage() {
        Long userId = 1L;
        String successMessage = "User deleted successfully";

        when(userService.deleteUser(userId)).thenReturn(successMessage);

        ResponseEntity<String> response = userController.deleteUser(userId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue()); // HttpStatus.OK
        assertEquals(successMessage, response.getBody());
        verify(userService, times(1)).deleteUser(userId);
    }
}
