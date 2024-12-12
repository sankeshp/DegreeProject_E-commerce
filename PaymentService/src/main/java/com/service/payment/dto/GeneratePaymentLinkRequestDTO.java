package com.service.payment.dto;

import lombok.Getter;
import lombok.Setter;
import com.service.payment.models.PGVendor;

@Getter
@Setter
public class GeneratePaymentLinkRequestDTO {
    private Long orderId;
    private PGVendor pgVendor;
}
