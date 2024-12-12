package com.service.payment.services;

import com.service.payment.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
   // http://localhost:8081/api/public/orders/{{orderId}}
   @Autowired
   private RestTemplate restTemplate;

    public Order getOrderById(Long orderId)
    {
        return restTemplate.getForObject("http://localhost:8081/api/public/orders/{orderId}", Order.class, orderId);
    }
}
