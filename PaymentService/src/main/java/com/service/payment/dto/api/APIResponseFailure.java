package com.service.payment.dto.api;

import lombok.Getter;
import java.util.Arrays;

@Getter
public class APIResponseFailure extends APIResponse {
    private final String message;
    private final String stackTrace;

    public APIResponseFailure(Exception ex) {
        this.message = ex.getMessage();
        this.stackTrace = Arrays.toString(ex.getStackTrace());
    }
}
