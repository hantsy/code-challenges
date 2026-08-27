package com.example.demo.domain.service;

import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.port.out.TransactionLoader;
import com.example.demo.port.out.TransactionPersister;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionLoadServiceTest {

    @Test
    void persists_all_transactions_loaded_from_the_source() {
        var loaded = List.of(
                new Transaction("WLMFRDGD", LocalDateTime.of(2020, 8, 20, 12, 45, 33),
                        new BigDecimal("59.99"), "Kwik-E-Mart", TransactionType.PAYMENT, null),
                new Transaction("YGXKOEIA", LocalDateTime.of(2020, 8, 20, 12, 46, 17),
                        new BigDecimal("10.95"), "Kwik-E-Mart", TransactionType.PAYMENT, null)
        );
        var loader = new FixedTransactionLoader(loaded);
        var persister = new RecordingTransactionPersister();

        new TransactionLoadService(loader, persister).loadAndPersist();

        assertThat(persister.persisted).isEqualTo(loaded);
    }

    static final class FixedTransactionLoader implements TransactionLoader {
        private final List<Transaction> transactions;

        FixedTransactionLoader(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        @Override
        public List<Transaction> load() {
            return transactions;
        }
    }

    static final class RecordingTransactionPersister implements TransactionPersister {
        List<Transaction> persisted = List.of();

        @Override
        public void persist(List<Transaction> data) {
            this.persisted = data;
        }
    }
}
