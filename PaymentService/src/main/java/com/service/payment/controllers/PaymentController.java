package com.service.payment.controllers;

import com.service.payment.dto.GeneratePaymentLinkRequestDTO;
import com.service.payment.dto.GeneratePaymentLinkResponseDTO;
import com.service.payment.dto.api.APIResponse;
import com.service.payment.dto.api.APIResponseFailure;
import com.service.payment.dto.api.APIResponseSuccess;
import com.service.payment.exceptions.InvalidPaymentGatewayException;
import com.service.payment.exceptions.PaymentLinkGenerationException;
import com.service.payment.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/generatePaymentLink")
    public ResponseEntity<APIResponse> generatePaymentLink(@RequestBody GeneratePaymentLinkRequestDTO requestDTO){
        APIResponse response;
        HttpStatus httpStatus = HttpStatus.CREATED;
        try {
            String paymentLink = paymentService.generatePaymentLink(requestDTO.getOrderId(), requestDTO.getPgVendor());
            response = new APIResponseSuccess<GeneratePaymentLinkResponseDTO>(new GeneratePaymentLinkResponseDTO(requestDTO.getOrderId(), paymentLink));
        } catch (PaymentLinkGenerationException | InvalidPaymentGatewayException e) {
            response = new APIResponseFailure(e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(httpStatus).body(response);
    }
}
