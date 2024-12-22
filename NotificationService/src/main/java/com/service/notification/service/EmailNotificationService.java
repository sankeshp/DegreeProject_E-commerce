package com.service.notification.service;

import com.service.notification.config.MailConfig;
import com.service.notification.dtos.NotificationMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
@Primary
public class EmailNotificationService implements SendNotificationService{

    @Autowired
    private MailConfig mailConfig;

    @Override
    public void SendNotification(NotificationMessageDTO notificationMessage) {

        Session session = mailConfig.javaMailSender();

        sendEmail(session, notificationMessage.getTo(), notificationMessage.getSubject(), notificationMessage.getBody());

    }

    private static void sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);

            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("scalerDemo@gmail.com", "Team Scaler"));

            msg.setReplyTo(InternetAddress.parse("scalerDemo@gmail.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
