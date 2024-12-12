package com.service.payment.exceptions;

public class InvalidPaymentGatewayException extends Exception {
    public InvalidPaymentGatewayException(String message) {
        super(message);
    }
}
