package com.example.demo.application;

import java.math.BigDecimal;

public sealed interface TransactionStatisticsResponse
        permits TransactionStatisticsResponse.Found, TransactionStatisticsResponse.NotFound {

    record Found(int count, BigDecimal totalAmount, BigDecimal averageAmount) implements TransactionStatisticsResponse {
        @Override
        public String toString() {
            var templatedString = """
                    Number of transactions = %d
                    Total Transaction Value = %.2f
                    Average Transaction Value = %.2f
                    """;
            return templatedString.formatted(count, totalAmount, averageAmount);
        }
    }

    record NotFound() implements TransactionStatisticsResponse {
        @Override
        public String toString() {
            return "No transactions found.";
        }
    }
}
