package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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

    static Function<InputStream, List<Transaction>> LOAD_TRANSACTIONS = (InputStream source) -> {

        try (var reader = new BufferedReader(new InputStreamReader(source))) {
            return reader.lines().skip(1)
                    .map(PARSE_TRANSACTION_LINE)
                    .toList();
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
