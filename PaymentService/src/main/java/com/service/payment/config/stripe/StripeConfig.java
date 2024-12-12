package com.service.payment.config.stripe;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class StripeConfig {
    @Value("${stripe.authentication.publishable_key}")
    private String stripePublishableKey;

    @Value("${stripe.authentication.secret_key}")
    private String stripeSecretKey;
}
