package com.example.demo.infrastructure.persistence;

import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTransactionRepositoryTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2020, 8, 20, 12, 45, 33);

    private final InMemoryTransactionRepository store = new InMemoryTransactionRepository();

    @BeforeEach
    void setUp() {
        store.save(List.of(
                new Transaction("WLMFRDGD", BASE, new BigDecimal("59.99"), "Kwik-E-Mart", TransactionType.PAYMENT, null),
                new Transaction("LFVCTEYM", BASE.plusMinutes(5), new BigDecimal("5.00"), "MacLaren", TransactionType.PAYMENT, null),
                new Transaction("AKNBVHMN", BASE.plusMinutes(30), new BigDecimal("10.95"), "Kwik-E-Mart", TransactionType.REVERSAL, "YGXKOEIA")
        ));
    }

    @Test
    void finds_transactions_by_type() {
        assertThat(store.findByType(TransactionType.REVERSAL))
                .extracting(Transaction::id)
                .containsExactly("AKNBVHMN");
    }

    @Test
    void finds_transactions_by_merchant_date_range_and_type() {
        var found = store.findByMerchantAndDateRangeAndType(
                "Kwik-E-Mart", BASE.minusSeconds(1), BASE.plusMinutes(31), TransactionType.PAYMENT);

        assertThat(found).extracting(Transaction::id).containsExactly("WLMFRDGD");
    }

    @Test
    void excludes_transactions_on_the_range_boundaries() {
        var found = store.findByMerchantAndDateRangeAndType(
                "Kwik-E-Mart", BASE, BASE, TransactionType.PAYMENT);

        assertThat(found).isEmpty();
    }
}
