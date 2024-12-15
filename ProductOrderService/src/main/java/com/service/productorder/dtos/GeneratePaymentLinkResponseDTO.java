package com.service.productorder.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneratePaymentLinkResponseDTO {
    private Long orderId;
    private String paymentLink;
}

