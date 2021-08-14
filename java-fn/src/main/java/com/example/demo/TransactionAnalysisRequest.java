package com.example.demo;

import java.time.LocalDateTime;
import java.util.Objects;

public record TransactionAnalysisRequest(
        LocalDateTime fromDate,
        LocalDateTime toDate,
        String merchantName
) {
    public TransactionAnalysisRequest {
        Objects.requireNonNull(merchantName, "merchant name can not be null");
        Objects.requireNonNull(fromDate, "fromDate can not be null");
        Objects.requireNonNull(toDate, "toDate can not be null");
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate should before toDate");
        }
    }
}
