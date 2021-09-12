package com.example.demo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> queryByMerchantAndDateRange(
            String merchant,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) throws IOException;
}
