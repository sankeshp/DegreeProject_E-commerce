package com.service.payment.paymentgateway;

import com.service.payment.dto.Order;
import com.service.payment.exceptions.PaymentLinkGenerationException;

public interface IPaymentGateway {
    String generatePaymentLink(Order order) throws PaymentLinkGenerationException;
}
