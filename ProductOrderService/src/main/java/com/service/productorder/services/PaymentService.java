package com.service.productorder.services;

import com.service.productorder.entites.Order;
import com.service.productorder.entites.Payment;
import com.service.productorder.dtos.GeneratePaymentLinkRequestDTO;
import com.service.productorder.dtos.GeneratePaymentLinkResponseDTO;
import com.service.productorder.entites.PaymentStatus;
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

        String url = "http://PaymentService/payment/generatePaymentLink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        GeneratePaymentLinkRequestDTO requestBody = new GeneratePaymentLinkRequestDTO(order.getOrderId(), paymentMethod);

        HttpEntity<GeneratePaymentLinkRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GeneratePaymentLinkResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GeneratePaymentLinkResponseDTO.class
        );

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentLink(Objects.requireNonNull(response.getBody()).getPaymentLink());
        payment.setPaymentStatus(PaymentStatus.Success);

        paymentRepo.save(payment);

       return payment;

    }
}
