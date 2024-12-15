package com.service.productorder.controllers;

import com.service.productorder.config.KafkaNotificationProducerClient;
import com.service.productorder.dtos.*;
import com.service.productorder.services.OrderService;
import com.service.productorder.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private KafkaNotificationProducerClient kafkaNotificationProducerClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGetAllOrders() {
        OrderResponseDTO mockResponse = new OrderResponseDTO();
        when(orderService.getAllOrders(anyInt(), anyInt(), anyString(), anyString())).thenReturn(mockResponse);

        ResponseEntity<OrderResponseDTO> response = orderController.getAllOrders(1, 10, "date", "asc");

        assertEquals(302, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(orderService, times(1)).getAllOrders(1, 10, "date", "asc");
    }

    @Test
    void testGetAllOrdersForUser() {
        String emailId = "test@example.com";
        UserDTO mockUser = new UserDTO();
        mockUser.setEmail(emailId);
        List<OrderDTO> mockOrders = Arrays.asList(new OrderDTO(), new OrderDTO());

        when(valueOperations.get("getOrdersByUser_" + emailId)).thenReturn(null);
        when(userService.getUserByEmail(emailId)).thenReturn(mockUser);
        when(orderService.getOrdersByUser(mockUser)).thenReturn(mockOrders);

        ResponseEntity<List<OrderDTO>> response = orderController.getAllOrdersForUser(emailId);

        assertEquals(302, response.getStatusCodeValue());
        assertEquals(mockOrders, response.getBody());
        verify(userService, times(1)).getUserByEmail(emailId);
        verify(orderService, times(1)).getOrdersByUser(mockUser);
        verify(redisTemplate.opsForValue(), times(1)).set("getOrdersByUser_" + emailId, mockOrders);
    }

    @Test
    void testGetOrderByUserAndOrderId() {
        String emailId = "test@example.com";
        Long orderId = 1L;
        UserDTO mockUser = new UserDTO();
        mockUser.setEmail(emailId);
        mockUser.setUserId(1L);
        OrderDTO mockOrder = new OrderDTO();

        when(valueOperations.get("getOrdersByUser_" + emailId)).thenReturn(null);
        when(userService.getUserByEmail(emailId)).thenReturn(mockUser);
        when(orderService.getOrder(1L, orderId)).thenReturn(mockOrder);

        ResponseEntity<OrderDTO> response = orderController.getOrderByUserAndOrderId(emailId, orderId);

        assertEquals(302, response.getStatusCodeValue());
        assertEquals(mockOrder, response.getBody());
        verify(userService, times(1)).getUserByEmail(emailId);
        verify(orderService, times(1)).getOrder(1L, orderId);
        verify(redisTemplate.opsForValue(), times(1)).set("getOrdersByUser_" + emailId, mockOrder);
    }

    @Test
    void testGetOrderById() {
        Long orderId = 1L;
        OrderDTO mockOrder = new OrderDTO();

        when(valueOperations.get("getOrderById_" + orderId)).thenReturn(null);
        when(orderService.getOrder(orderId)).thenReturn(mockOrder);

        ResponseEntity<OrderDTO> response = orderController.getOrderById(orderId);

        assertEquals(302, response.getStatusCodeValue());
        assertEquals(mockOrder, response.getBody());
        verify(orderService, times(1)).getOrder(orderId);
        verify(redisTemplate.opsForValue(), times(1)).set("getOrderById_" + orderId, mockOrder);
    }

    @Test
    void testOrderProducts() {
        String emailId = "test@example.com";
        Long cartId = 1L;
        String paymentMethod = "CreditCard";
        UserDTO mockUser = new UserDTO();
        mockUser.setEmail(emailId);
        mockUser.setUserId(1L);
        OrderDTO mockOrder = new OrderDTO();

        when(userService.getUserByEmail(emailId)).thenReturn(mockUser);
        when(orderService.placeOrder(1L, cartId, paymentMethod)).thenReturn(mockOrder);

        ResponseEntity<OrderDTO> response = orderController.orderProducts(emailId, cartId, paymentMethod);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockOrder, response.getBody());
        verify(userService, times(1)).getUserByEmail(emailId);
        verify(orderService, times(1)).placeOrder(1L, cartId, paymentMethod);
        verify(kafkaNotificationProducerClient, times(1)).publishPaymentEvent(any(SendNotificationMessageDTO.class));
    }

    @Test
    void testUpdateOrderByUser() {
        String emailId = "test@example.com";
        Long orderId = 1L;
        String orderStatus = "Shipped";
        UserDTO mockUser = new UserDTO();
        mockUser.setEmail(emailId);
        mockUser.setUserId(1L);
        OrderDTO mockOrder = new OrderDTO();

        when(userService.getUserByEmail(emailId)).thenReturn(mockUser);
        when(orderService.updateOrder(1L, orderId, orderStatus)).thenReturn(mockOrder);

        ResponseEntity<OrderDTO> response = orderController.updateOrderByUser(emailId, orderId, orderStatus);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockOrder, response.getBody());
        verify(userService, times(1)).getUserByEmail(emailId);
        verify(orderService, times(1)).updateOrder(1L, orderId, orderStatus);
    }
}
