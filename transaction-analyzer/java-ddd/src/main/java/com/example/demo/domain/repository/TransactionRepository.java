package com.example.demo.domain.repository;

import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    void save(List<Transaction> transactions);

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type);
}
