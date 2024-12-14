package com.service.payment.services;

import com.service.payment.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

   @Autowired
   private RestTemplate restTemplate;

    public OrderDTO getOrderById(Long orderId)
    {
        return restTemplate.getForObject("http://localhost:8081/api/public/orders/{orderId}", OrderDTO.class, orderId);
    }
}
