package com.example.demo.domain.service;

import com.example.demo.domain.model.Notification;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.port.in.QueryValidPaymentTransactionsPort;
import com.example.demo.port.out.Notifier;
import com.example.demo.port.out.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Returns all transactions for the given merchant within the date range, excluding:
 * 1. all `REVERSAL` transactions;
 * 2. `PAYMENT` transactions that have an existing `REVERSAL` transaction related to them.
 */
public class TransactionQueryService implements QueryValidPaymentTransactionsPort {
    private static final Logger LOGGER = Logger.getLogger(TransactionQueryService.class.getName());

    private final TransactionRepository store;
    private final List<Notifier> notifiers;

    public TransactionQueryService(TransactionRepository store, List<Notifier> notifiers) {
        this.store = store;
        this.notifiers = List.copyOf(notifiers);
    }

    @Override
    public List<Transaction> queryValidPaymentTransactions(String merchant, LocalDateTime fromDate, LocalDateTime toDate) {
        var reversalRelatedTransactionIds = this.store.findByType(TransactionType.REVERSAL)
                .stream()
                .map(Transaction::relatedTransactionId)
                .toList();

        var transactions = this.store.findByMerchantAndDateRangeAndType(
                merchant,
                fromDate,
                toDate,
                TransactionType.PAYMENT
        );

        var filtered = transactions.stream()
                .filter(t -> !reversalRelatedTransactionIds.contains(t.id()))
                .toList();
        LOGGER.log(Level.INFO, "{0} transactions found.", filtered.size());

        var notification = new Notification("queryValidPaymentTransactions is executed.", LocalDateTime.now());
        this.notifiers.forEach(notifier -> notifier.notify(notification));
        return filtered;
    }
}
