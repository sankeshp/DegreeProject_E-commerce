package com.service.productorder.controllers;

import com.service.productorder.dtos.CartDTO;
import com.service.productorder.dtos.UserDTO;
import com.service.productorder.services.CartService;
import com.service.productorder.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private CartDTO cartDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        cartDTO = new CartDTO();
        cartDTO.setCartId(1L);

        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setEmail("test@example.com");
    }

    @Test
    void getAllCarts_ShouldReturnListOfCarts() {
        List<CartDTO> carts = List.of(cartDTO);
        when(cartService.getAllCarts()).thenReturn(carts);

        ResponseEntity<List<CartDTO>> response = cartController.getAllCarts();

        assertEquals(302, response.getStatusCodeValue()); // HttpStatus.FOUND = 302
        assertEquals(carts, response.getBody());
        verify(cartService, times(1)).getAllCarts();
    }

    @Test
    void addProductToCart_ShouldReturnUpdatedCart() {
        when(cartService.addProductToCart(1L, 1L, 10)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.addProductToCart(1L, 1L, 10);

        assertEquals(201, response.getStatusCodeValue()); // HttpStatus.CREATED = 201
        assertEquals(cartDTO, response.getBody());
        verify(cartService, times(1)).addProductToCart(1L, 1L, 10);
    }

    @Test
    void addCartForUser_ShouldReturnNewCart() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDTO);
        when(cartService.createCart(1L)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.addCartForUser("test@example.com");

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(cartDTO, response.getBody());
        verify(userService, times(1)).getUserByEmail("test@example.com");
        verify(cartService, times(1)).createCart(1L);
    }

    @Test
    void updateCartProduct_ShouldReturnUpdatedCart() {
        when(cartService.updateProductQuantityInCart(1L, 1L, 5)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.updateCartProduct(1L, 1L, 5);

        assertEquals(200, response.getStatusCodeValue()); // HttpStatus.OK = 200
        assertEquals(cartDTO, response.getBody());
        verify(cartService, times(1)).updateProductQuantityInCart(1L, 1L, 5);
    }

    @Test
    void deleteProductFromCart_ShouldReturnSuccessMessage() {
        String message = "Product removed successfully";
        when(cartService.deleteProductFromCart(1L, 1L)).thenReturn(message);

        ResponseEntity<String> response = cartController.deleteProductFromCart(1L, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(message, response.getBody());
        verify(cartService, times(1)).deleteProductFromCart(1L, 1L);
    }
}
