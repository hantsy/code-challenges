package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public sealed class TransactionAnalysisResult
        permits TransactionAnalysisResult.Found, TransactionAnalysisResult.NotFound {

    static final class Found extends TransactionAnalysisResult {
        private final long count;
        private final BigDecimal totalAmount;
        private final BigDecimal averageAmount;

        public Found(List<Transaction> filteredTransactions) {
            this.count = filteredTransactions.size();
            this.totalAmount = filteredTransactions.stream()
                    .map(Transaction::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.averageAmount = totalAmount.divide(new BigDecimal(count), RoundingMode.HALF_UP);
        }

        public long getCount() {
            return count;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public BigDecimal getAverageAmount() {
            return averageAmount;
        }

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

    static final class NotFound extends TransactionAnalysisResult {
        @Override
        public String toString() {
            return "No transactions found.";
        }
    }
}
