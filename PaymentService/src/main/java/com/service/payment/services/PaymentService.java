package com.service.payment.services;

import com.service.payment.dto.OrderDTO;
import com.service.payment.exceptions.InvalidPaymentGatewayException;
import com.service.payment.exceptions.PaymentLinkGenerationException;
import com.service.payment.models.PGVendor;
import com.service.payment.paymentgateway.IPaymentGateway;
import com.service.payment.strategies.PaymentGatewaySelectorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentGatewaySelectorStrategy paymentGatewaySelectorStrategy;

    @Autowired
    private OrderService orderService;

    public PaymentService(PaymentGatewaySelectorStrategy paymentGatewaySelectorStrategy) {
        this.paymentGatewaySelectorStrategy = paymentGatewaySelectorStrategy;
    }

    public String generatePaymentLink(Long orderId, PGVendor pgVendor) throws PaymentLinkGenerationException, InvalidPaymentGatewayException {

        OrderDTO orderDTO = orderService.getOrderById(orderId);

        IPaymentGateway paymentGateway = paymentGatewaySelectorStrategy.getPaymentGateway(pgVendor);
        return paymentGateway.generatePaymentLink(orderDTO);
    }
}
