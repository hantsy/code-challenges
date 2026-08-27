package com.example.demo.port.in;

import com.example.demo.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface QueryValidPaymentTransactionsPort {
    List<Transaction> queryValidPaymentTransactions(String merchant, LocalDateTime fromDate, LocalDateTime toDate);
}
