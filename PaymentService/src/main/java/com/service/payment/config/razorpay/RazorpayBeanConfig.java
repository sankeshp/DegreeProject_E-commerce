package com.service.payment.config.razorpay;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayBeanConfig {

    private final RazorpayConfig razorpayConfig;

    public RazorpayBeanConfig(RazorpayConfig razorpayConfig) {
        this.razorpayConfig = razorpayConfig;
    }

    @Bean
    public RazorpayClient getRazorpayClient() throws RazorpayException {
        return new RazorpayClient(razorpayConfig.getRazorpayKeyId(), razorpayConfig.getRazorpayKeySecret());
    }
}
