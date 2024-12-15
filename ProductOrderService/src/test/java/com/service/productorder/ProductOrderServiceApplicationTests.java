package com.service.productorder;

import com.service.productorder.config.KafkaNotificationProducerClient;
import com.service.productorder.dtos.SendNotificationMessageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;

@SpringBootTest
class ProductOrderServiceApplicationTests {
}
