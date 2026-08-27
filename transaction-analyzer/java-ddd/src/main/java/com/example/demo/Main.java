package com.example.demo;

import com.example.demo.adapter.in.console.ConsoleTransactionReportAdapter;
import com.example.demo.adapter.out.csv.CsvTransactionLoaderAdapter;
import com.example.demo.adapter.out.notification.EmailNotifierAdapter;
import com.example.demo.adapter.out.notification.SlackNotifierAdapter;
import com.example.demo.adapter.out.persistence.InMemoryTransactionStoreAdapter;
import com.example.demo.domain.service.TransactionLoadService;
import com.example.demo.domain.service.TransactionQueryService;
import com.example.demo.domain.service.TransactionStatisticsReportService;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // outbound adapters
        var store = new InMemoryTransactionStoreAdapter();
        var loader = new CsvTransactionLoaderAdapter(Main.class.getResourceAsStream("/input.csv"));
        var notifiers = List.of(
                new SlackNotifierAdapter(),
                new EmailNotifierAdapter(),
                notification -> LOGGER.info("Sent from a dummy notifier")
        );

        // domain services implementing the inbound ports
        var loadService = new TransactionLoadService(loader, store);
        loadService.loadAndPersist();

        var queryService = new TransactionQueryService(store, notifiers);
        var reportService = new TransactionStatisticsReportService(queryService);

        // inbound adapter
        var console = new ConsoleTransactionReportAdapter(reportService);
        console.run(new Scanner(System.in));
    }
}
