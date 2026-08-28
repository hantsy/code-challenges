package com.example.demo;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FunctionsTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2020, 8, 20, 12, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2020, 8, 20, 15, 0, 0);

    private static TransactionAnalysisApplication.TransactionAnalysisRequest request() {
        return new TransactionAnalysisApplication.TransactionAnalysisRequest(FROM, TO, "Kwik-E-Mart");
    }

    private static TransactionAnalysisApplication.Transaction payment(String id, BigDecimal amount) {
        return new TransactionAnalysisApplication.Transaction(id, FROM.plusMinutes(10), amount, "Kwik-E-Mart",
                TransactionAnalysisApplication.TransactionType.PAYMENT, null);
    }

    private static TransactionAnalysisApplication.Transaction reversal(String id, BigDecimal amount, String relatedId) {
        return new TransactionAnalysisApplication.Transaction(id, FROM.plusMinutes(30), amount, "Kwik-E-Mart",
                TransactionAnalysisApplication.TransactionType.REVERSAL, relatedId);
    }

    @Test
    public void testParseTransactionLine() {
        var line = "WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,";

        var result = TransactionAnalysisApplication.Functions.parseTransactionLine.apply(line);

        assertThat(result.id()).isEqualTo("WLMFRDGD");
        assertThat(result.merchantName()).isEqualTo("Kwik-E-Mart");
        assertThat(result.type().name()).isEqualTo("PAYMENT");
        assertThat(result.amount()).isCloseTo(new BigDecimal("59.99"), Offset.offset(new BigDecimal("0.01")));
        assertThat(result.transactedAt().getYear()).isEqualTo(2020);
        assertThat(result.relatedTransactionId()).isNullOrEmpty();
    }

    @Test
    void transforms_lines_into_transactions() {
        var lines = List.of(
                "WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,",
                "YGXKOEIA, 20/08/2020 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,"
        );

        var result = TransactionAnalysisApplication.Functions.toTransactionList.apply(lines);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("WLMFRDGD");
        assertThat(result.get(1).id()).isEqualTo("YGXKOEIA");
    }

    @Test
    void transforms_an_empty_list_into_an_empty_list() {
        var result = TransactionAnalysisApplication.Functions.toTransactionList.apply(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void analyzes_found_transactions() {
        var data = List.of(
                payment("WLMFRDGD", new BigDecimal("59.99")),
                payment("YGXKOEIA", new BigDecimal("5.00"))
        );

        var result = TransactionAnalysisApplication.Functions.analyze.apply(request()).apply(data);

        assertThat(result).isInstanceOf(TransactionAnalysisApplication.TransactionAnalysisResult.Found.class);
        var found = (TransactionAnalysisApplication.TransactionAnalysisResult.Found) result;
        assertThat(found.count()).isEqualTo(2);
        assertThat(found.totalAmount()).isEqualByComparingTo(new BigDecimal("64.99"));
        assertThat(found.averageAmount()).isEqualByComparingTo(new BigDecimal("32.50"));
    }

    @Test
    void analyzes_not_found_when_there_are_no_valid_transactions() {
        var result = TransactionAnalysisApplication.Functions.analyze.apply(request()).apply(List.of());

        assertThat(result).isInstanceOf(TransactionAnalysisApplication.TransactionAnalysisResult.NotFound.class);
    }

    @Test
    void excludes_payments_that_have_a_related_reversal() {
        var data = List.of(
                payment("WLMFRDGD", new BigDecimal("59.99")),
                payment("YGXKOEIA", new BigDecimal("10.95")),
                reversal("AKNBVHMN", new BigDecimal("10.95"), "YGXKOEIA")
        );

        var result = TransactionAnalysisApplication.Functions.analyze.apply(request()).apply(data);

        var found = (TransactionAnalysisApplication.TransactionAnalysisResult.Found) result;
        assertThat(found.count()).isEqualTo(1);
        assertThat(found.totalAmount()).isEqualByComparingTo(new BigDecimal("59.99"));
    }

    @Test
    void filters_by_merchant_and_date_range() {
        var otherMerchant = new TransactionAnalysisApplication.Transaction("OTH0001", FROM.plusMinutes(10), new BigDecimal("9.99"), "Moe's Tavern",
                TransactionAnalysisApplication.TransactionType.PAYMENT, null);
        var outsideRange = new TransactionAnalysisApplication.Transaction("OUT0001", TO.plusMinutes(10), new BigDecimal("9.99"), "Kwik-E-Mart",
                TransactionAnalysisApplication.TransactionType.PAYMENT, null);
        var data = List.of(payment("WLMFRDGD", new BigDecimal("59.99")), otherMerchant, outsideRange);

        var result = TransactionAnalysisApplication.Functions.analyze.apply(request()).apply(data);

        var found = (TransactionAnalysisApplication.TransactionAnalysisResult.Found) result;
        assertThat(found.count()).isEqualTo(1);
        assertThat(found.totalAmount()).isEqualByComparingTo(new BigDecimal("59.99"));
    }

    @Test
    void unchecked_bridges_checked_exceptions_into_runtime_exceptions() {
        TransactionAnalysisApplication.CheckedFunction<String, String> failing = path -> {
            throw new IOException("boom");
        };

        assertThatThrownBy(() -> failing.unchecked().apply("input.csv"))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void checked_function_composes_with_andThen() throws Exception {
        TransactionAnalysisApplication.CheckedFunction<String, Integer> length = String::length;
        TransactionAnalysisApplication.CheckedFunction<Integer, Double> half = value -> value / 2.0;

        var result = length.andThen(half).apply("hello");

        assertThat(result).isEqualTo(2.5);
    }
}
