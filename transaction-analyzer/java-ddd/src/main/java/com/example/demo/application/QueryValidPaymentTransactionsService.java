package com.example.demo.application;

import com.example.demo.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Returns all transactions for the given merchant within the date range, excluding:
 * 1. all {@code REVERSAL} transactions;
 * 2. {@code PAYMENT} transactions that have an existing {@code REVERSAL} transaction related to them.
 */
public interface QueryValidPaymentTransactionsService {
    List<Transaction> queryValidPaymentTransactions(String merchant, LocalDateTime fromDate, LocalDateTime toDate);
}
