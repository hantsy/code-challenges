package com.example.demo.application.internal;

import com.example.demo.application.QueryValidPaymentTransactionsService;
import com.example.demo.domain.model.Notification;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.domain.repository.TransactionRepository;
import com.example.demo.domain.service.NotificationSender;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultQueryValidPaymentTransactionsServiceTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2020, 8, 20, 12, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2020, 8, 20, 15, 0, 0);

    private final FakeTransactionRepository store = new FakeTransactionRepository();
    private final CollectingNotificationSender notifier = new CollectingNotificationSender();
    private final QueryValidPaymentTransactionsService service = new DefaultQueryValidPaymentTransactionsService(store, List.of(notifier));

    @Test
    void excludes_reversed_payments_from_the_result() {
        var reversed = payment("YGXKOEIA", "Kwik-E-Mart", "10.95");
        store.payments.add(reversed);
        store.payments.add(payment("WLMFRDGD", "Kwik-E-Mart", "59.99"));
        store.reversals.add(new Transaction(
                "AKNBVHMN", FROM.plusMinutes(30), new BigDecimal("10.95"),
                "Kwik-E-Mart", TransactionType.REVERSAL, reversed.id()));

        var result = service.queryValidPaymentTransactions("Kwik-E-Mart", FROM, TO);

        assertThat(result).hasSize(1)
                .extracting(Transaction::id)
                .containsExactly("WLMFRDGD");
    }

    @Test
    void notifies_all_registered_notifiers_after_a_query() {
        store.payments.add(payment("WLMFRDGD", "Kwik-E-Mart", "59.99"));

        service.queryValidPaymentTransactions("Kwik-E-Mart", FROM, TO);

        assertThat(notifier.received).hasSize(1)
                .first()
                .extracting(Notification::message)
                .isEqualTo("queryValidPaymentTransactions is executed.");
    }

    private static Transaction payment(String id, String merchant, String amount) {
        return new Transaction(id, FROM.plusMinutes(10), new BigDecimal(amount), merchant, TransactionType.PAYMENT, null);
    }

    static final class FakeTransactionRepository implements TransactionRepository {
        final List<Transaction> payments = new ArrayList<>();
        final List<Transaction> reversals = new ArrayList<>();

        @Override
        public void save(List<Transaction> transactions) {
        }

        @Override
        public List<Transaction> findByType(TransactionType type) {
            return type == TransactionType.REVERSAL ? reversals : payments;
        }

        @Override
        public List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type) {
            return findByType(type).stream()
                    .filter(it -> it.merchantName().equals(merchant)
                            && it.transactedAt().isAfter(fromDate)
                            && it.transactedAt().isBefore(toDate))
                    .toList();
        }
    }

    static final class CollectingNotificationSender implements NotificationSender {
        final List<Notification> received = new ArrayList<>();

        @Override
        public void notify(Notification notification) {
            received.add(notification);
        }
    }
}
