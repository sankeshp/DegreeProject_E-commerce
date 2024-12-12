package com.service.payment.exceptions;

public class PaymentLinkGenerationException extends Exception {
    public PaymentLinkGenerationException(Exception ex) {
        super(ex);
    }
}
