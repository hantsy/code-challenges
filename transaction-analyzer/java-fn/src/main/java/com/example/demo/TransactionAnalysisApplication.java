package com.example.demo;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static com.example.demo.TransactionAnalysisApplication.Functions.*;
import static java.util.Collections.emptyList;

public class TransactionAnalysisApplication {
    public final static DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("fromDate (dd/MM/yyyy HH:mm:ss):");
        var fromDate = scanner.nextLine();
        System.out.println("toDate (dd/MM/yyyy HH:mm:ss):");
        var toDate = scanner.nextLine();
        System.out.println("merchant:");
        var merchant = scanner.nextLine();

        var file = "./input.csv";


        var request = new TransactionAnalysisRequest(
                LocalDateTime.parse(fromDate, customFormatter),
                LocalDateTime.parse(toDate, customFormatter),
                merchant
        );

        //loading from a local file
        CompletableFuture.supplyAsync(() -> readTransactionFile.apply(file))
                .thenApply(transformToTransactionList)
                // compute the transaction analysis data according to the analysis request input by user
                // generate readable analysis report
                .thenCombine(CompletableFuture.completedFuture(request), computeTransactionAnalysisResult)
                // print the analysis result(simply via System.out)
                .thenAccept(printTransactionAnalysisResult)
                // make it happens.
                .join();
    }

    //AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
    public static record Transaction(
            String id,
            LocalDateTime transactedAt,
            BigDecimal amount,
            String merchantName,
            TransactionType type,
            String relatedTransactionId
    ) {
    }

    public static class Functions {
        private final static Logger LOGGER = Logger.getLogger(Functions.class.getSimpleName());

        static Function<String, List<String>> readTransactionFile = (String filePath) -> {
            try {
                return Files.readAllLines(Paths.get(filePath)).stream().skip(1).toList();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("file path: '" + filePath + "' was not found.");
            }
        };

        static Function<String, Transaction> parseTransactionLine = (String line) -> {
            LOGGER.info("reading line:" + line);
            var fields = line.split(",");
            LOGGER.info("fields: " + fields.length);
            return new Transaction(
                    fields[0].trim(),
                    LocalDateTime.parse(fields[1].trim(), customFormatter),
                    new BigDecimal(fields[2].trim()),
                    fields[3].trim(),
                    TransactionType.valueOf(fields[4].trim()),
                    fields.length == 6 ? fields[5].trim() : null
            );
        };

        static Function<List<String>, List<Transaction>> transformToTransactionList = (List<String> source) -> {
            LOGGER.info("sources: " + source);
            Function<List<String>, List<Transaction>> toTransactionList = (List<String> src) -> src.stream()
                    .map(parseTransactionLine)
                    .toList();
            return source.isEmpty() ? emptyList() : toTransactionList.apply(source);
        };

        static BiFunction<List<Transaction>, TransactionAnalysisRequest, TransactionAnalysisResult> computeTransactionAnalysisResult =
                (List<Transaction> data, TransactionAnalysisRequest request) -> {
                    LOGGER.info("transaction data source: " + data);
                    LOGGER.info("input request: " + request);
                    var reversalRelatedIds = data.stream()
                            .filter(it -> it.type() == TransactionType.REVERSAL)
                            .map(Transaction::relatedTransactionId)
                            .toList();
                    LOGGER.info("reversalRelatedIds: " + reversalRelatedIds);

                    Predicate<Transaction> validTransaction = (Transaction it) ->
                            // merchant name matches
                            it.merchantName().equals(request.merchantName())
                                    // satisfies the date range.
                                    && it.transactedAt().isAfter(request.fromDate())
                                    && it.transactedAt().isBefore(request.toDate())
                                    // only the `PAYMENT` type is calculated into the analysis.
                                    && it.type() == TransactionType.PAYMENT
                                    // this payment record should not contain a reversal record.
                                    && !reversalRelatedIds.contains(it.id());

                    var filteredTransactions = data.stream()
                            .filter(validTransaction)
                            .toList();
                    LOGGER.info("filteredTransactions: " + filteredTransactions);
                    if (filteredTransactions.isEmpty()) {
                        return new TransactionAnalysisResult.NotFound();
                    } else {
                        return new TransactionAnalysisResult.Found(filteredTransactions);
                    }
                };

        static Consumer<TransactionAnalysisResult> printTransactionAnalysisResult = System.out::println;
    }

    public static record TransactionAnalysisRequest(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String merchantName
    ) {
        public TransactionAnalysisRequest {
            Objects.requireNonNull(merchantName, "merchant name can not be null");
            Objects.requireNonNull(fromDate, "fromDate can not be null");
            Objects.requireNonNull(toDate, "toDate can not be null");
            if (fromDate.isAfter(toDate)) {
                throw new IllegalArgumentException("fromDate should be before toDate");
            }
        }
    }

    public sealed static class TransactionAnalysisResult
            permits TransactionAnalysisResult.Found, TransactionAnalysisResult.NotFound {

        static final class Found extends TransactionAnalysisResult {
            private final long count;
            private final BigDecimal totalAmount;
            private final BigDecimal averageAmount;

            public Found(List<Transaction> filteredTransactions) {
                this.count = filteredTransactions.size();
                this.totalAmount = filteredTransactions.stream()
                        .map(Transaction::amount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                this.averageAmount = totalAmount.divide(new BigDecimal(count), RoundingMode.HALF_UP);
            }

            public long getCount() {
                return count;
            }

            public BigDecimal getTotalAmount() {
                return totalAmount;
            }

            public BigDecimal getAverageAmount() {
                return averageAmount;
            }

            @Override
            public String toString() {

                var templatedString = """
                        Number of transactions = %d
                        Total Transaction Value = %.2f
                        Average Transaction Value = %.2f
                        """;
                return templatedString.formatted(count, totalAmount, averageAmount);
            }
        }

        static final class NotFound extends TransactionAnalysisResult {
            @Override
            public String toString() {
                return "No transactions found.";
            }
        }
    }

    enum TransactionType {
        PAYMENT,
        REVERSAL,
    }
}

