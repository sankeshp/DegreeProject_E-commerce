package com.service.payment.paymentgateway;

import com.service.payment.dto.OrderDTO;
import com.service.payment.exceptions.PaymentLinkGenerationException;

public interface IPaymentGateway {
    String generatePaymentLink(OrderDTO orderDTO) throws PaymentLinkGenerationException;
}
