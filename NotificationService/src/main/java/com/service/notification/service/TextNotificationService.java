package com.service.notification.service;

import com.service.notification.config.TwilioConfig;
import com.service.notification.dtos.NotificationMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TextNotificationService implements SendNotificationService{

    @Autowired
    private TwilioConfig twilioConfig;


    @Override
    public void SendNotification(NotificationMessageDTO notificationMessage) {
        String recipientPhoneNumber = notificationMessage.getNumber().toString(); // Replace with actual user's phone number

        String messageBody = "Hello, "+notificationMessage.getSubject()+" "+notificationMessage.getBody();

        Message message = Message.creator(
                new PhoneNumber(recipientPhoneNumber),  // Recipient's phone number
                new PhoneNumber(twilioConfig.getPhoneNumber()),    // Twilio phone number
                messageBody                            // SMS body
        ).create();

        System.out.println("SMS sent successfully. Message SID: " + message.getSid());
    }
}
