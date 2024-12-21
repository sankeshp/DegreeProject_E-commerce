package com.service.productorder.config;

import com.service.productorder.dtos.SendNotificationMessageDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaNotificationProducerClient {
    private static final String TOPIC = "notification-topic";
    private final KafkaTemplate<String, SendNotificationMessageDTO> kafkaTemplate;

    public KafkaNotificationProducerClient(KafkaTemplate<String, SendNotificationMessageDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishNotificationEvent(SendNotificationMessageDTO sendNotificationMessageDTO) {
        kafkaTemplate.send(TOPIC, sendNotificationMessageDTO);
        System.out.println("Published notification event: " + sendNotificationMessageDTO);
    }
}