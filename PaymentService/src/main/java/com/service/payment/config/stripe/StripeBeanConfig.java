package com.service.payment.config.stripe;

import com.stripe.StripeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeBeanConfig {
    private final StripeConfig stripeConfig;

    public StripeBeanConfig(StripeConfig stripeConfig) {
        this.stripeConfig = stripeConfig;
    }

    @Bean
    public StripeClient getStripeClient(){
        return new StripeClient(this.stripeConfig.getStripeSecretKey());
    }
}
