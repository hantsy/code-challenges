package com.example.demo.adapter.in.console;

import com.example.demo.port.in.GenerateTransactionStatisticsReportPort;
import com.example.demo.port.in.TransactionStatisticsRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ConsoleTransactionReportAdapter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final GenerateTransactionStatisticsReportPort reportPort;

    public ConsoleTransactionReportAdapter(GenerateTransactionStatisticsReportPort reportPort) {
        this.reportPort = reportPort;
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

        System.out.println(this.reportPort.generateReport(request));
    }
}
