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
    void finds_payments_by_merchant_and_date_range() {
        var found = store.findValidPayments(
                "Kwik-E-Mart", BASE.minusSeconds(1), BASE.plusMinutes(31));

        assertThat(found).extracting(Transaction::id).containsExactly("WLMFRDGD");
    }

    @Test
    void excludes_transactions_on_the_range_boundaries() {
        var found = store.findValidPayments("Kwik-E-Mart", BASE, BASE);

        assertThat(found).isEmpty();
    }

    @Test
    void excludes_payments_that_have_a_related_reversal() {
        store.save(List.of(
                payment("WLMFRDGD", "59.99"),
                payment("YGXKOEIA", "10.95"),
                reversal("AKNBVHMN", "YGXKOEIA")
        ));

        var found = store.findValidPayments(
                "Kwik-E-Mart", BASE.minusHours(1), BASE.plusHours(1));

        assertThat(found).extracting(Transaction::id).containsExactly("WLMFRDGD");
    }

    @Test
    void ignores_a_reversal_that_has_no_related_transaction_id() {
        store.save(List.of(
                payment("WLMFRDGD", "59.99"),
                reversal("AKNBVHMN", null)
        ));

        var found = store.findValidPayments(
                "Kwik-E-Mart", BASE.minusHours(1), BASE.plusHours(1));

        assertThat(found).extracting(Transaction::id).containsExactly("WLMFRDGD");
    }

    private static Transaction payment(String id, String amount) {
        return new Transaction(id, BASE, new BigDecimal(amount),
                "Kwik-E-Mart", TransactionType.PAYMENT, null);
    }

    private static Transaction reversal(String id, String relatedTransactionId) {
        return new Transaction(id, BASE.plusMinutes(30), new BigDecimal("10.95"),
                "Kwik-E-Mart", TransactionType.REVERSAL, relatedTransactionId);
    }
}
