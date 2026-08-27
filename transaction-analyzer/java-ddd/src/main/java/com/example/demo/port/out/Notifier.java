package com.example.demo.port.out;

import com.example.demo.domain.model.Notification;

public interface Notifier {
    void notify(Notification notification);
}
