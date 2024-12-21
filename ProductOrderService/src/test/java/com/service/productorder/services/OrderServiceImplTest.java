package com.service.productorder.services;

import com.service.productorder.dtos.*;
import com.service.productorder.entites.*;
import com.service.productorder.exceptions.APIException;
import com.service.productorder.exceptions.ResourceNotFoundException;
import com.service.productorder.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private OrderItemRepo orderItemRepo;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPlaceOrder_Success() {
        Long userId = 1L;
        Long cartId = 1L;
        String paymentMethod = "CreditCard";

        Cart cart = new Cart();
        cart.setTotalPrice(500.0);
        cart.setCartItems(Collections.singletonList(new CartItem(1L, new Product(1L, "Product1", 10, 100.0), 2, 10.0, 200.0)));

        Order savedOrder = new Order();
        savedOrder.setOrderId(1L);

        when(cartRepo.findCartByEmailAndCartId(userId, cartId)).thenReturn(cart);
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepo.saveAll(anyList())).thenReturn(Collections.singletonList(new OrderItem()));
        when(paymentService.makePayment(any(Order.class), eq(paymentMethod))).thenReturn(new Payment());
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        OrderDTO result = orderService.placeOrder(userId, cartId, paymentMethod);

        assertNotNull(result);
        verify(orderRepo, times(2)).save(any(Order.class));
        verify(orderItemRepo).saveAll(anyList());
        verify(paymentService).makePayment(any(Order.class), eq(paymentMethod));
    }

    @Test
    void testPlaceOrder_CartNotFound() {
        when(cartRepo.findCartByEmailAndCartId(anyLong(), anyLong())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder(1L, 1L, "CreditCard"));
    }

    @Test
    void testPlaceOrder_EmptyCart() {
        Cart cart = new Cart();
        cart.setCartItems(Collections.emptyList());

        when(cartRepo.findCartByEmailAndCartId(anyLong(), anyLong())).thenReturn(cart);

        assertThrows(APIException.class, () -> orderService.placeOrder(1L, 1L, "CreditCard"));
    }

    @Test
    void testGetOrdersByUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(1L);

        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderRepo.findAllByUserId(userDTO.getUserId())).thenReturn(orders);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        List<OrderDTO> result = orderService.getOrdersByUser(userDTO);

        assertEquals(2, result.size());
        verify(orderRepo).findAllByUserId(userDTO.getUserId());
    }

    @Test
    void testGetOrdersByUser_NoOrders() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(1L);

        when(orderRepo.findAllByUserId(userDTO.getUserId())).thenReturn(Collections.emptyList());

        assertThrows(APIException.class, () -> orderService.getOrdersByUser(userDTO));
    }

    @Test
    void testGetOrderById_Success() {
        Order order = new Order();
        when(orderRepo.findOrderByOrderId(1L)).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        OrderDTO result = orderService.getOrder(1L);

        assertNotNull(result);
        verify(orderRepo).findOrderByOrderId(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepo.findOrderByOrderId(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrder(1L));
    }

    @Test
    void testGetAllOrders_Success() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        Page<Order> pageOrders = new PageImpl<>(orders);

        when(orderRepo.findAll(any(PageRequest.class))).thenReturn(pageOrders);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        OrderResponseDTO result = orderService.getAllOrders(0, 10, "orderId", "asc");

        assertEquals(2, result.getContent().size());
        verify(orderRepo).findAll(any(PageRequest.class));
    }

    @Test
    void testGetAllOrders_NoOrders() {
        Page<Order> pageOrders = new PageImpl<>(Collections.emptyList());
        when(orderRepo.findAll(any(PageRequest.class))).thenReturn(pageOrders);

        assertThrows(APIException.class, () -> orderService.getAllOrders(0, 10, "orderId", "asc"));
    }

    @Test
    void testUpdateOrder_Success() {
        Order order = new Order();
        when(orderRepo.findOrderByEmailAndOrderId(anyLong(), anyLong())).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        OrderDTO result = orderService.updateOrder(1L, 1L, "Shipped");

        assertNotNull(result);
        assertEquals(OrderStatus.Shipped, order.getOrderStatus());
        verify(orderRepo).findOrderByEmailAndOrderId(anyLong(), anyLong());
    }

    @Test
    void testUpdateOrder_NotFound() {
        when(orderRepo.findOrderByEmailAndOrderId(anyLong(), anyLong())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L, 1L, "Shipped"));
    }
}
