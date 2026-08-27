package com.example.demo.domain.service;

import com.example.demo.domain.model.Transaction;
import com.example.demo.port.in.GenerateTransactionStatisticsReportPort;
import com.example.demo.port.in.QueryValidPaymentTransactionsPort;
import com.example.demo.port.in.TransactionStatisticsRequest;
import com.example.demo.port.in.TransactionStatisticsResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionStatisticsReportService implements GenerateTransactionStatisticsReportPort {

    private final QueryValidPaymentTransactionsPort queryPort;

    public TransactionStatisticsReportService(QueryValidPaymentTransactionsPort queryPort) {
        this.queryPort = queryPort;
    }

    @Override
    public TransactionStatisticsResponse generateReport(TransactionStatisticsRequest request) {
        var filtered = this.queryPort.queryValidPaymentTransactions(
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
