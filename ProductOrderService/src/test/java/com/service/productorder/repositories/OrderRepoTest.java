package com.service.productorder.repositories;

import com.service.productorder.entites.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class OrderRepoTest {

    @Mock
    private OrderRepo orderRepo;

    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        order1 = new Order();
        order1.setUserId(1L);
        order1.setOrderId(1001L);

        order2 = new Order();
        order2.setUserId(1L);
        order2.setOrderId(1002L);
    }

    @Test
    void testFindOrderByEmailAndOrderId() {

        when(orderRepo.findOrderByEmailAndOrderId(1L, 1L)).thenReturn(order1);

        Order result = orderRepo.findOrderByEmailAndOrderId(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getOrderId()).isEqualTo(1001L);
    }

    @Test
    void testFindAllByUserId() {

        when(orderRepo.findAllByUserId(1L)).thenReturn(Arrays.asList(order1, order2));


        List<Order> orders = orderRepo.findAllByUserId(1L);


        assertThat(orders).isNotEmpty();
        assertThat(orders.size()).isEqualTo(2);
        assertThat(orders).extracting(Order::getOrderId).contains(1001L, 1002L);
    }

    @Test
    void testFindOrderByOrderId() {

        when(orderRepo.findOrderByOrderId(1001L)).thenReturn(order1);

        Order result = orderRepo.findOrderByOrderId(1001L);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1001L);
        assertThat(result.getUserId()).isEqualTo(1L);
    }
}
