package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.domain.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {

    private List<Transaction> data = List.of();

    @Override
    public void save(List<Transaction> transactions) {
        // in a real world application, it maybe calls database operations or invokes remote requests.
        this.data = List.copyOf(transactions);
    }

    @Override
    public List<Transaction> findValidPayments(String merchant, LocalDateTime fromDate, LocalDateTime toDate) {
        // a reversal without a related id excludes nothing, and toUnmodifiableSet rejects nulls
        var reversedTransactionIds = this.data.stream()
                .filter(it -> it.type() == TransactionType.REVERSAL)
                .map(Transaction::relatedTransactionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

        return this.data.stream()
                .filter(it -> it.type() == TransactionType.PAYMENT)
                .filter(it -> it.merchantName().equals(merchant))
                .filter(it -> it.transactedAt().isAfter(fromDate))
                .filter(it -> it.transactedAt().isBefore(toDate))
                .filter(it -> !reversedTransactionIds.contains(it.id()))
                .toList();
    }
}
