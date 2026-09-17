package com.example.demo.application.internal;

import com.example.demo.application.GenerateTransactionStatisticsReportService;
import com.example.demo.application.TransactionStatisticsRequest;
import com.example.demo.application.TransactionStatisticsResponse;
import com.example.demo.domain.model.Notification;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.repository.TransactionRepository;
import com.example.demo.domain.service.NotificationSender;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultGenerateTransactionStatisticsReportService implements GenerateTransactionStatisticsReportService {
    private static final Logger LOGGER = Logger.getLogger(DefaultGenerateTransactionStatisticsReportService.class.getName());

    private final TransactionRepository store;
    private final List<NotificationSender> notifiers;

    public DefaultGenerateTransactionStatisticsReportService(TransactionRepository store, List<NotificationSender> notifiers) {
        this.store = store;
        this.notifiers = List.copyOf(notifiers);
    }

    @Override
    public TransactionStatisticsResponse generateReport(TransactionStatisticsRequest request) {
        var filtered = this.store.findValidPayments(
                request.merchantName(),
                request.fromDate(),
                request.toDate()
        );
        LOGGER.log(Level.INFO, "{0} transactions found.", filtered.size());

        var notification = new Notification("generateReport is executed.", LocalDateTime.now());
        this.notifiers.forEach(notifier -> notifier.notify(notification));

        if (filtered.isEmpty()) {
            return new TransactionStatisticsResponse.NotFound();
        }

        var count = filtered.size();
        var sum = filtered.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var avg = sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
        return new TransactionStatisticsResponse.Found(count, sum, avg);
    }
}
