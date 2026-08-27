package com.example.demo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(
        String id,
        LocalDateTime transactedAt,
        BigDecimal amount,
        String merchantName,
        TransactionType type,
        String relatedTransactionId
) {
}
