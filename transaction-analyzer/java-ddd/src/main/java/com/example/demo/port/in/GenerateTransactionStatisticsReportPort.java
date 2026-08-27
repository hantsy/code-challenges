package com.example.demo.port.in;

public interface GenerateTransactionStatisticsReportPort {
    TransactionStatisticsResponse generateReport(TransactionStatisticsRequest request);
}
