package com.example.demo.port.out;

import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type);
}
