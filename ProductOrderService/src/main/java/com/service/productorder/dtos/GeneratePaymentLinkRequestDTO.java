package com.service.productorder.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratePaymentLinkRequestDTO {
    private Long orderId;
    private String pgVendor;

    public GeneratePaymentLinkRequestDTO(Long orderId, String paymentMethod) {
        this.orderId = orderId;
        this.pgVendor = paymentMethod;
    }
}
