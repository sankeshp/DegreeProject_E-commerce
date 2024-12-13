package com.service.notification.service;

import com.service.notification.dtos.NotificationMessageDTO;

public interface SendNotificationService {
    void SendNotification(NotificationMessageDTO notificationMessage);
}
