package com.service.payment.paymentgateway;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.service.payment.dto.Order;
import com.service.payment.exceptions.PaymentLinkGenerationException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class RazorpayPaymentGateway implements IPaymentGateway {

    private RazorpayClient razorpayClient;

    public RazorpayPaymentGateway(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    @Override
    public String generatePaymentLink(Order order) throws PaymentLinkGenerationException {
        String paymentLink = null;
        Long expireBy = LocalDateTime.now().plusDays(1).atZone(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();

        try {
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", order.getTotalAmount());
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("expire_by", expireBy);
            paymentLinkRequest.put("reference_id", order.getOrderId());
            paymentLinkRequest.put("description", "Payment service requesting payment for orderId : " + order.getOrderId());

            paymentLinkRequest.put("callback_url", "http://localhost:8080");
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
            paymentLink = payment.get("short_url");
        } catch (RazorpayException ex) {
            throw new PaymentLinkGenerationException(ex);
        }
        return paymentLink;
    }
}
