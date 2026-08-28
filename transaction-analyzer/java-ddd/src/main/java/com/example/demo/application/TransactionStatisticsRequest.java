package com.example.demo.application;

import java.time.LocalDateTime;
import java.util.Objects;

public record TransactionStatisticsRequest(
        LocalDateTime fromDate,
        LocalDateTime toDate,
        String merchantName
) {
    public TransactionStatisticsRequest {
        Objects.requireNonNull(merchantName, "merchant name can not be null");
        Objects.requireNonNull(fromDate, "fromDate can not be null");
        Objects.requireNonNull(toDate, "toDate can not be null");
        if (toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("fromDate should before toDate");
        }
    }
}
