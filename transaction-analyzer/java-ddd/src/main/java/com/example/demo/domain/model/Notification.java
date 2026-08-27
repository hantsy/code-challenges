package com.example.demo.domain.model;

import java.time.LocalDateTime;

public record Notification(
        String message,
        LocalDateTime sentAt
) {
}
