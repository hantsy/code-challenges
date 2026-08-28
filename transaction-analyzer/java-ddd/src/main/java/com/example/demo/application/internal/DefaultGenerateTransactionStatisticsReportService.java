package com.example.demo.application.internal;

import com.example.demo.application.GenerateTransactionStatisticsReportService;
import com.example.demo.application.QueryValidPaymentTransactionsService;
import com.example.demo.application.TransactionStatisticsRequest;
import com.example.demo.application.TransactionStatisticsResponse;
import com.example.demo.domain.model.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DefaultGenerateTransactionStatisticsReportService implements GenerateTransactionStatisticsReportService {

    private final QueryValidPaymentTransactionsService queryService;

    public DefaultGenerateTransactionStatisticsReportService(QueryValidPaymentTransactionsService queryService) {
        this.queryService = queryService;
    }

    @Override
    public TransactionStatisticsResponse generateReport(TransactionStatisticsRequest request) {
        var filtered = this.queryService.queryValidPaymentTransactions(
                request.merchantName(),
                request.fromDate(),
                request.toDate()
        );

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
