package com.example.demo;

import com.example.demo.application.internal.DefaultGenerateTransactionStatisticsReportService;
import com.example.demo.application.internal.DefaultLoadTransactionsService;
import com.example.demo.application.internal.DefaultQueryValidPaymentTransactionsService;
import com.example.demo.infrastructure.csv.CsvTransactionLoader;
import com.example.demo.infrastructure.notification.EmailNotificationSender;
import com.example.demo.infrastructure.notification.SlackNotificationSender;
import com.example.demo.infrastructure.persistence.InMemoryTransactionRepository;
import com.example.demo.interfaces.console.TransactionReportConsole;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // infrastructure
        var store = new InMemoryTransactionRepository();
        var loader = new CsvTransactionLoader(Main.class.getResourceAsStream("/input.csv"));
        var notifiers = List.of(
                new SlackNotificationSender(),
                new EmailNotificationSender(),
                notification -> LOGGER.info("Sent from a dummy notifier")
        );

        // application services
        var loadService = new DefaultLoadTransactionsService(loader, store);
        loadService.loadAndPersist();

        var queryService = new DefaultQueryValidPaymentTransactionsService(store, notifiers);
        var reportService = new DefaultGenerateTransactionStatisticsReportService(queryService);

        // interfaces
        var console = new TransactionReportConsole(reportService);
        console.run(new Scanner(System.in));
    }
}
