package com.example.demo;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

public class Functions {

    static Function<String, Transaction> PARSE_TRANSACTION_LINE = (String line) -> {
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

    static Supplier<List<Transaction>> LOAD_TRANSACTIONS = () -> {
        try {
            return Files.lines(Paths.get("./input.csv"), StandardCharsets.UTF_8)
                    .skip(1)// skip first line.
                    .map(PARSE_TRANSACTION_LINE)
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    };

    static BiFunction<List<Transaction>, TransactionAnalysisRequest, TransactionAnalysisResult> COMPUTE_TRANSACTION_ANALYSIS =
            (List<Transaction> data, TransactionAnalysisRequest request) -> {
                var reversalRelatedIds = data.stream()
                        .filter(it -> it.type() == TransactionType.REVERSAL)
                        .map(Transaction::relatedTransactionId)
                        .toList();
                var filteredTransactions = data.stream()
                        .filter(it -> it.merchantName().equals(request.merchantName())
                                && it.transactedAt().isAfter(request.fromDate())
                                && it.transactedAt().isBefore(request.toDate())
                                && it.type() == TransactionType.PAYMENT
                                && !reversalRelatedIds.contains(it.id())
                        )
                        .toList();

                if (filteredTransactions.isEmpty()) {
                    return new TransactionAnalysisResult.NotFound();
                } else {
                    return new TransactionAnalysisResult.Found(filteredTransactions);
                }
            };

    static Consumer<TransactionAnalysisResult> PRINT_ANALYSIS_RESULT = System.out::println;
}
