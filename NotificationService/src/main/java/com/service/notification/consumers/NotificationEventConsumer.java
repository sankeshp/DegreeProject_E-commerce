package com.service.notification.consumers;

import com.service.notification.dtos.NotificationMessageDTO;
import com.service.notification.service.SendNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventConsumer {

    @Autowired
    private SendNotificationService sendNotificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-service-group")
    public void handleSendEmailEvent(NotificationMessageDTO notificationMessageDTO) {
        sendNotificationService.SendNotification(notificationMessageDTO);
  }
}
