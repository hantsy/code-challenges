package com.example.demo;

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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

import static com.example.demo.TransactionAnalysisApplication.Functions.*;

public class TransactionAnalysisApplication {
    public static final DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

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

        // loading from a local file, analyzing, then printing the report as a fluent pipeline
        CompletableFuture.supplyAsync(() -> loadTransactionsFromFile.apply(file))
                .thenApply(analyze.apply(request))
                .thenAccept(printTransactionAnalysisResult)
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

    /**
     * A {@link Function} that may throw a checked exception, so it can be composed
     * fluently with other functions and bridged back to {@link Function} via
     * {@link #unchecked()}.
     */
    @FunctionalInterface
    interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;

        default <V> CheckedFunction<T, V> andThen(CheckedFunction<? super R, ? extends V> after) {
            return input -> after.apply(apply(input));
        }

        default Function<T, R> unchecked() {
            return input -> {
                try {
                    return apply(input);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        }

        static <T, R> CheckedFunction<T, R> of(CheckedFunction<T, R> function) {
            return function;
        }
    }

    public static class Functions {
        private final static Logger LOGGER = Logger.getLogger(Functions.class.getSimpleName());

        static final Function<String, List<String>> readTransactionFile = CheckedFunction
                .<String, List<String>>of(path -> Files.readAllLines(Paths.get(path)))
                .andThen(lines -> lines.stream().skip(1).toList())
                .unchecked();

        static final Function<String, Transaction> parseTransactionLine = (String line) -> {
            LOGGER.info("reading line: " + line);
            var fields = line.split(",");
            return new Transaction(
                    fields[0].trim(),
                    LocalDateTime.parse(fields[1].trim(), customFormatter),
                    new BigDecimal(fields[2].trim()),
                    fields[3].trim(),
                    TransactionType.valueOf(fields[4].trim()),
                    fields.length == 6 ? fields[5].trim() : null
            );
        };

        static final Function<List<String>, List<Transaction>> toTransactionList = (List<String> source) -> source.stream()
                .map(parseTransactionLine)
                .toList();

        static final Function<String, List<Transaction>> loadTransactionsFromFile = readTransactionFile
                .andThen(toTransactionList);

        static final Function<TransactionAnalysisRequest, Function<List<Transaction>, TransactionAnalysisResult>> analyze =
                (TransactionAnalysisRequest request) -> (List<Transaction> data) -> computeTransactionAnalysis(data, request);

        static TransactionAnalysisResult computeTransactionAnalysis(List<Transaction> data, TransactionAnalysisRequest request) {
            LOGGER.info("transaction data source: " + data);
            LOGGER.info("input request: " + request);

            var reversalRelatedIds = data.stream()
                    .filter(it -> it.type() == TransactionType.REVERSAL)
                    .map(Transaction::relatedTransactionId)
                    .toList();
            LOGGER.info("reversalRelatedIds: " + reversalRelatedIds);

            // merchant name matches, satisfies the date range, only the `PAYMENT` type is
            // counted, and this payment record should not have a related reversal record.
            var filteredTransactions = data.stream()
                    .filter(it -> it.merchantName().equals(request.merchantName())
                            && it.transactedAt().isAfter(request.fromDate())
                            && it.transactedAt().isBefore(request.toDate())
                            && it.type() == TransactionType.PAYMENT
                            && !reversalRelatedIds.contains(it.id()))
                    .toList();
            LOGGER.info("filteredTransactions: " + filteredTransactions);

            if (filteredTransactions.isEmpty()) {
                return new TransactionAnalysisResult.NotFound();
            }

            var count = filteredTransactions.size();
            var totalAmount = filteredTransactions.stream()
                    .map(Transaction::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            var averageAmount = totalAmount.divide(new BigDecimal(count), RoundingMode.HALF_UP);
            return new TransactionAnalysisResult.Found(count, totalAmount, averageAmount);
        }

        static final Consumer<TransactionAnalysisResult> printTransactionAnalysisResult = System.out::println;
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

    public sealed interface TransactionAnalysisResult
            permits TransactionAnalysisResult.Found, TransactionAnalysisResult.NotFound {

        record Found(long count, BigDecimal totalAmount, BigDecimal averageAmount) implements TransactionAnalysisResult {
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

        record NotFound() implements TransactionAnalysisResult {
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

