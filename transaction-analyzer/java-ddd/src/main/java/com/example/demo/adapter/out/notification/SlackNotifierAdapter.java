package com.example.demo.adapter.out.notification;

import com.example.demo.port.out.Notifier;
import com.example.demo.domain.model.Notification;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SlackNotifierAdapter implements Notifier {
    private static final Logger LOGGER = Logger.getLogger(SlackNotifierAdapter.class.getName());

    @Override
    public void notify(Notification notification) {
        LOGGER.log(Level.INFO, "send notification to slack channel: {0}", new Object[]{notification});
        //...
    }
}
