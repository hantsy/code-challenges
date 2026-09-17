package com.example.demo.domain.repository;

import com.example.demo.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    void save(List<Transaction> transactions);

    /**
     * Returns all transactions for the given merchant within the date range, excluding:
     * 1. all {@code REVERSAL} transactions;
     * 2. {@code PAYMENT} transactions that have an existing {@code REVERSAL} transaction related to them.
     * <p>
     * The date range is exclusive on both ends: a transaction transacted exactly at
     * {@code fromDate} or {@code toDate} is not included.
     */
    List<Transaction> findValidPayments(String merchant, LocalDateTime fromDate, LocalDateTime toDate);
}
