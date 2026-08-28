package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.domain.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

public class InMemoryTransactionRepository implements TransactionRepository {

    private List<Transaction> data = List.of();

    @Override
    public void save(List<Transaction> transactions) {
        // in a real world application, it maybe calls database operations or invokes remote requests.
        this.data = List.copyOf(transactions);
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return this.data.stream()
                .filter(it -> it.type() == type)
                .toList();
    }

    @Override
    public List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type) {
        return this.data.stream()
                .filter(it -> it.merchantName().equals(merchant)
                        && it.transactedAt().isAfter(fromDate)
                        && it.transactedAt().isBefore(toDate)
                        && it.type() == type
                )
                .toList();
    }
}
