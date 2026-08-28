package com.example.demo.infrastructure.notification;

import com.example.demo.domain.model.Notification;
import com.example.demo.domain.service.NotificationSender;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailNotificationSender implements NotificationSender {
    private static final Logger LOGGER = Logger.getLogger(EmailNotificationSender.class.getName());

    @Override
    public void notify(Notification notification) {
        LOGGER.log(Level.INFO, "send notification by email: {0}", new Object[]{notification});
        //...
    }
}
