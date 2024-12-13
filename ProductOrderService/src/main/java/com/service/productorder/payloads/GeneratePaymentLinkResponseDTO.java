package com.service.productorder.payloads;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneratePaymentLinkResponseDTO {
    private Long orderId;
    private String paymentLink;
}

