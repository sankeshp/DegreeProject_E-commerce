package com.service.payment.strategies;

import com.service.payment.exceptions.InvalidPaymentGatewayException;
import com.service.payment.models.PGVendor;
import com.service.payment.paymentgateway.IPaymentGateway;
import com.service.payment.paymentgateway.RazorpayPaymentGateway;
import com.service.payment.paymentgateway.StripePaymentGateway;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewaySelectorStrategy {
    private final RazorpayPaymentGateway razorpayPaymentGateway;
    private final StripePaymentGateway stripePaymentGateway;

    public PaymentGatewaySelectorStrategy(RazorpayPaymentGateway razorpayPaymentGateway, StripePaymentGateway stripePaymentGateway) {
        this.razorpayPaymentGateway = razorpayPaymentGateway;
        this.stripePaymentGateway = stripePaymentGateway;
    }

    public IPaymentGateway getPaymentGateway(PGVendor pgVendor) throws InvalidPaymentGatewayException {
        if (pgVendor == PGVendor.RAZORPAY) {
            return razorpayPaymentGateway;
        } else if (pgVendor == PGVendor.STRIPE) {
            return stripePaymentGateway;
        }
        throw new InvalidPaymentGatewayException("Unable to generate object of payment gateway of type : "+ pgVendor);
    }
}
