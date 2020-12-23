import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

public class Main {

    static Function<String, Transaction> TRANSACTION_LINE_MAPPER = (String line) -> {
        System.out.println("reading line:" + line);
        var fields = line.split(",");
        System.out.println("fields: " + fields.length);
        return new Transaction(
                fields[0].trim(),
                LocalDateTime.parse(fields[1].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                new BigDecimal(fields[2].trim()),
                fields[3].trim(),
                TransactionType.valueOf(fields[4].trim()),
                fields.length == 6 ? fields[5].trim() : null
        );
    };

    static Supplier<List<Transaction>> TRANSACTION_READER = () -> {
        try {
            return Files.lines(Paths.get("./input.csv"), StandardCharsets.UTF_8)
                    .skip(1)// skip first line.
                    .map(TRANSACTION_LINE_MAPPER)
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    };

    static BiFunction<List<Transaction>, TransactionStatisticsRequest, List<Transaction>> TRANSACTION_ANALYSIS =
            (List<Transaction> data, TransactionStatisticsRequest request) -> {
                var reversalRelatedIds = data.stream()
                        .filter(it -> it.type() == TransactionType.REVERSAL)
                        .map(Transaction::relatedTransactionId)
                        .collect(toList());
                return data.stream()
                        .filter(it -> it.merchantName().equals(request.merchantName())
                                && it.transactedAt().isAfter(request.fromDate())
                                && it.transactedAt().isBefore(request.toDate())
                                && it.type() == TransactionType.PAYMENT
                                && !reversalRelatedIds.contains(it.id())
                        ).collect(toList());
            };

    static Function<List<Transaction>, TransactionStatisticsResponse> TRANSACTION_REPORT =
            (List<Transaction> filteredTransactions) -> {

                if (filteredTransactions.isEmpty()) {
                    return new TransactionStatisticsResponse.NotFound();
                } else {
                    var count = filteredTransactions.size();
                    var sum = filteredTransactions.stream()
                            .map(Transaction::amount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    var avg = sum.divide(new BigDecimal(count), RoundingMode.HALF_UP);
                    return new TransactionStatisticsResponse.Found(count, sum, avg);
                }
            };

    static Consumer<TransactionStatisticsResponse> TRANSACTION_PRINTER = System.out::println;

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("fromDate (dd/MM/yyyy HH:mm:ss):");
        var fromDate = scanner.nextLine();
        System.out.println("toDate (dd/MM/yyyy HH:mm:ss):");
        var toDate = scanner.nextLine();
        System.out.println("merchant:");
        var merchant = scanner.nextLine();

        CompletableFuture.supplyAsync(TRANSACTION_READER)
                .thenCombine(CompletableFuture.completedFuture(
                        new TransactionStatisticsRequest(
                                LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                                LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                                merchant
                        )),
                        TRANSACTION_ANALYSIS
                )
                .thenApply(TRANSACTION_REPORT)
                .thenAccept(TRANSACTION_PRINTER)
                .join();
    }
}

record TransactionStatisticsRequest(
        LocalDateTime fromDate,
        LocalDateTime toDate,
        String merchantName
) {
    public TransactionStatisticsRequest {
        Objects.requireNonNull(merchantName, "merchant name can not be null");
        Objects.requireNonNull(fromDate, "fromDate can not be null");
        Objects.requireNonNull(toDate, "toDate can not be null");
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate should before toDate");
        }
    }
}

sealed class TransactionStatisticsResponse
        permits TransactionStatisticsResponse.Found, TransactionStatisticsResponse.NotFound {

    static final class Found extends TransactionStatisticsResponse {
        private final long count;
        private final BigDecimal totalAmount;
        private final BigDecimal averageAmount;

        public Found(long count, BigDecimal totalAmount, BigDecimal averageAmount) {
            this.count = count;
            this.totalAmount = totalAmount;
            this.averageAmount = averageAmount;
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

    static final class NotFound extends TransactionStatisticsResponse {
        @Override
        public String toString() {
            return "No transactions found.";
        }
    }
}

//AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
record Transaction(
        String id,
        LocalDateTime transactedAt,
        BigDecimal amount,
        String merchantName,
        TransactionType type,
        String relatedTransactionId
) {
}

enum TransactionType {
    PAYMENT,
    REVERSAL,
}

