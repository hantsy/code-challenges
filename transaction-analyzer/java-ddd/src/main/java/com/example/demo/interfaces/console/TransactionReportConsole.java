package com.example.demo.interfaces.console;

import com.example.demo.application.GenerateTransactionStatisticsReportService;
import com.example.demo.application.TransactionStatisticsRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TransactionReportConsole {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final GenerateTransactionStatisticsReportService reportService;

    public TransactionReportConsole(GenerateTransactionStatisticsReportService reportService) {
        this.reportService = reportService;
    }

    public void run(Scanner scanner) {
        System.out.println("fromDate (dd/MM/yyyy HH:mm:ss):");
        var fromDate = scanner.nextLine();
        System.out.println("toDate (dd/MM/yyyy HH:mm:ss):");
        var toDate = scanner.nextLine();
        System.out.println("merchant:");
        var merchant = scanner.nextLine();

        var request = new TransactionStatisticsRequest(
                LocalDateTime.parse(fromDate.trim(), DATE_FORMAT),
                LocalDateTime.parse(toDate.trim(), DATE_FORMAT),
                merchant.trim()
        );

        System.out.println(this.reportService.generateReport(request));
    }
}
