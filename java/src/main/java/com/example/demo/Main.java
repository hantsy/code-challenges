package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("fromDate (dd/MM/yyyy HH:mm:ss):");
        var fromDate = scanner.nextLine();
        System.out.println("toDate (dd/MM/yyyy HH:mm:ss):");
        var toDate = scanner.nextLine();
        System.out.println("merchant:");
        var merchant = scanner.nextLine();

        //load file
        var input = Main.class.getResourceAsStream("input.csv");

        //parse and query
        var result = new TransactionRepository(new InputStreamTransactionLoader(input))
                .queryByMerchantAndDateRange(
                        merchant,
                        LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                );

        //print result
        if (result.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            String printTemplate = """
                    Number of transactions = %d
                    Total Transaction Value = %.2f
                    Average Transaction Value = %.2f
                    """;
            var sum = result.stream()
                    .map(Transaction::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            var avg = sum.divide(new BigDecimal(result.size()), RoundingMode.HALF_UP);
            System.out.printf((printTemplate) + "%n", result.size(), sum, avg);
        }
    }
}



