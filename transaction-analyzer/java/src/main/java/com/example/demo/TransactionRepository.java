package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    void persist(List<Transaction> data);
    List<Transaction> queryByMerchantAndDateRange(
            String merchant,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );
}
