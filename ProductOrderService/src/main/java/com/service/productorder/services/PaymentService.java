package com.service.productorder.services;

import com.service.productorder.entites.Order;
import com.service.productorder.entites.Payment;
import com.service.productorder.dtos.GeneratePaymentLinkRequestDTO;
import com.service.productorder.dtos.GeneratePaymentLinkResponseDTO;
import com.service.productorder.repositories.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepo paymentRepo;

    public Payment makePayment(Order order, String paymentMethod) {

        String url = "http://localhost:8082/payment/generatePaymentLink";

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        GeneratePaymentLinkRequestDTO requestBody = new GeneratePaymentLinkRequestDTO(order.getOrderId(), paymentMethod);

        // Create HttpEntity containing headers and body
        HttpEntity<GeneratePaymentLinkRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        // Perform the request
        ResponseEntity<GeneratePaymentLinkResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET, // HTTP method
                entity,
                GeneratePaymentLinkResponseDTO.class    // Response type
        );

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentLink(Objects.requireNonNull(response.getBody()).getPaymentLink());

        paymentRepo.save(payment);

       return payment;

    }
}
