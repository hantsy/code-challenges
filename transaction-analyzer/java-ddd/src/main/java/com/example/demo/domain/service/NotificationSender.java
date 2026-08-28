package com.example.demo.domain.service;

import com.example.demo.domain.model.Notification;

public interface NotificationSender {
    void notify(Notification notification);
}
