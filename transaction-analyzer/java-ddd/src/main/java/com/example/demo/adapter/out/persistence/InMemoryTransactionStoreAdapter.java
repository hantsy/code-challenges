package com.example.demo.adapter.out.persistence;

import com.example.demo.port.out.TransactionPersister;
import com.example.demo.port.out.TransactionRepository;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public class InMemoryTransactionStoreAdapter implements TransactionPersister, TransactionRepository {

    private List<Transaction> data = List.of();

    @Override
    public void persist(List<Transaction> data) {
        // in a real world application, it maybe calls database operations or invokes remote requests.
        this.data = List.copyOf(data);
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
