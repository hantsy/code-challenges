package com.example.demo.application.internal;

import com.example.demo.application.QueryValidPaymentTransactionsService;
import com.example.demo.domain.model.Notification;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.domain.repository.TransactionRepository;
import com.example.demo.domain.service.NotificationSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultQueryValidPaymentTransactionsService implements QueryValidPaymentTransactionsService {
    private static final Logger LOGGER = Logger.getLogger(DefaultQueryValidPaymentTransactionsService.class.getName());

    private final TransactionRepository store;
    private final List<NotificationSender> notifiers;

    public DefaultQueryValidPaymentTransactionsService(TransactionRepository store, List<NotificationSender> notifiers) {
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
