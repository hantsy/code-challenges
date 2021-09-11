package com.example.demo;

import com.example.demo.TransactionType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class InMemoryTransactionRepository implements TransactionRepository {

    private List<Transaction> data;

    public InMemoryTransactionRepository(TransactionLoader loader) {
        try {
            this.data = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void persist(List<Transaction> data) {
        this.data = data;
    }

    @Override
    public List<Transaction> queryByMerchantAndDateRange(
            String merchant,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        var reversal = data.stream()
                .filter(it -> it.type() == TransactionType.REVERSAL)
                .map(Transaction::relatedTransactionId)
                .collect(Collectors.toList());
        return data.stream()
                .filter(it -> it.merchantName().equals(merchant)
                        && it.transactedAt().isAfter(fromDate)
                        && it.transactedAt().isBefore(toDate)
                        && it.type() == TransactionType.PAYMENT
                        && !reversal.contains(it.id())
                )
                .collect(Collectors.toList());
    }

}
