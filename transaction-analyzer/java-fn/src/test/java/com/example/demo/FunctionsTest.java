package com.example.demo;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionsTest {

    @BeforeEach
    void setUp() {
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
}