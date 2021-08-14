package com.example.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import static com.example.demo.Functions.*;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("fromDate (dd/MM/yyyy HH:mm:ss):");
        var fromDate = scanner.nextLine();
        System.out.println("toDate (dd/MM/yyyy HH:mm:ss):");
        var toDate = scanner.nextLine();
        System.out.println("merchant:");
        var merchant = scanner.nextLine();

        var source = Main.class.getResourceAsStream("/input.csv");

        var request = new TransactionAnalysisRequest(
                LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                merchant
        );

        //loading from a local file
        CompletableFuture.completedFuture(source)
                .thenApply(LOAD_TRANSACTIONS)
                // compute the transaction analysis data according to the analysis request input by user
                // generate readable analysis report
                .thenCombine(CompletableFuture.completedFuture(request), COMPUTE_TRANSACTION_ANALYSIS)
                // print the analysis result(simply via System.out)
                .thenAccept(PRINT_ANALYSIS_RESULT)
                // make it happens.
                .join();
    }
}

