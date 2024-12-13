package com.service.productorder.payloads;

import lombok.*;

@Getter
@Setter
@ToString
public class SendNotificationMessageDTO {
    private String to;
    private String from;
    private String subject;
    private String body;
    private Long number;
}