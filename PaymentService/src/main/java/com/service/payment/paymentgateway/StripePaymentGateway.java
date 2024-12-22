package com.service.payment.paymentgateway;

import com.service.payment.dto.OrderDTO;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentLinkCreateParams;
import com.service.payment.exceptions.PaymentLinkGenerationException;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentGateway implements IPaymentGateway{
    private final StripeClient stripeClient;

    public StripePaymentGateway(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public String generatePaymentLink(OrderDTO orderDTO) throws PaymentLinkGenerationException {
        String paymentLink = null;

        try{
            PaymentLinkCreateParams params =
                    PaymentLinkCreateParams.builder()
                            .addLineItem(
                                    PaymentLinkCreateParams.LineItem.builder()
                                            .setPrice(String.valueOf(orderDTO.getTotalAmount()))
                                            .setQuantity(1L)
                                            .build()
                            )
                            .setAfterCompletion(
                                    PaymentLinkCreateParams.AfterCompletion.builder()
                                            .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                            .setRedirect(
                                                    PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                                            .setUrl("https://scalerDemo.com")
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            PaymentLink stripePaymentLink = stripeClient.paymentLinks().create(params);
            paymentLink = stripePaymentLink.getUrl();
        }
        catch (StripeException ex){
            throw new PaymentLinkGenerationException(ex);
        }

        return paymentLink;
    }
}
