package com.example.demo.application.internal;

import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.service.TransactionLoader;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultLoadTransactionsServiceTest {

    @Test
    void persists_all_transactions_loaded_from_the_source() {
        var loaded = List.of(
                new Transaction("WLMFRDGD", LocalDateTime.of(2020, 8, 20, 12, 45, 33),
                        new BigDecimal("59.99"), "Kwik-E-Mart", TransactionType.PAYMENT, null),
                new Transaction("YGXKOEIA", LocalDateTime.of(2020, 8, 20, 12, 46, 17),
                        new BigDecimal("10.95"), "Kwik-E-Mart", TransactionType.PAYMENT, null)
        );
        var loader = new FixedTransactionLoader(loaded);
        var repository = new RecordingTransactionRepository();

        new DefaultLoadTransactionsService(loader, repository).loadAndPersist();

        assertThat(repository.saved).isEqualTo(loaded);
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

    static final class RecordingTransactionRepository implements TransactionRepository {
        List<Transaction> saved = List.of();

        @Override
        public void save(List<Transaction> transactions) {
            this.saved = transactions;
        }

        @Override
        public List<Transaction> findByType(TransactionType type) {
            return List.of();
        }

        @Override
        public List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type) {
            return List.of();
        }
    }
}
