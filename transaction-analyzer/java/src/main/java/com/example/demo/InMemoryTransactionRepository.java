package com.example.demo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


class InMemoryTransactionRepository implements TransactionRepository {

    private  final TransactionLoader loader;

    InMemoryTransactionRepository(TransactionLoader loader) {
        this.loader = loader;
    }

    @Override
    public List<Transaction> queryByMerchantAndDateRange(
            String merchant,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) throws IOException {
        var data = loader.load();
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
