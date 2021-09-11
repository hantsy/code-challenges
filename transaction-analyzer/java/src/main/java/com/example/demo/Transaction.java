package com.example.demo;

import com.example.demo.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
public record Transaction(
        String id,
        LocalDateTime transactedAt,
        BigDecimal amount,
        String merchantName,
        TransactionType type,
        String relatedTransactionId
) {
}
