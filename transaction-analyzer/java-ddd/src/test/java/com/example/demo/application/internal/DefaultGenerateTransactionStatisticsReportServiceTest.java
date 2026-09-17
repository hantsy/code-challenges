package com.example.demo.application.internal;

import com.example.demo.application.GenerateTransactionStatisticsReportService;
import com.example.demo.application.TransactionStatisticsRequest;
import com.example.demo.application.TransactionStatisticsResponse;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultGenerateTransactionStatisticsReportServiceTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2020, 8, 20, 12, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2020, 8, 20, 15, 0, 0);

    private final FixedTransactionRepository repository = new FixedTransactionRepository();
    private final CollectingNotificationSender notifier = new CollectingNotificationSender();
    private final GenerateTransactionStatisticsReportService service =
            new DefaultGenerateTransactionStatisticsReportService(repository, List.of(notifier));

    @Test
    void reports_statistics_of_found_transactions() {
        repository.result = List.of(
                payment("WLMFRDGD", "59.99"),
                payment("SUOVOISP", "5.00")
        );

        var response = service.generateReport(request());

        assertThat(response).isInstanceOf(TransactionStatisticsResponse.Found.class);
        var found = (TransactionStatisticsResponse.Found) response;
        assertThat(found.count()).isEqualTo(2);
        assertThat(found.totalAmount()).isEqualByComparingTo("64.99");
        assertThat(found.averageAmount()).isEqualByComparingTo("32.50");
    }

    @Test
    void reports_not_found_when_there_is_no_transaction() {
        repository.result = List.of();

        var response = service.generateReport(request());

        assertThat(response).isInstanceOf(TransactionStatisticsResponse.NotFound.class);
        assertThat(response.toString()).isEqualTo("No transactions found.");
    }

    @Test
    void notifies_all_registered_notifiers_after_generating_a_report() {
        repository.result = List.of(payment("WLMFRDGD", "59.99"));

        service.generateReport(request());

        assertThat(notifier.received).hasSize(1)
                .first()
                .extracting(Notification::message)
                .isEqualTo("generateReport is executed.");
    }

    @Test
    void notifies_even_when_no_transaction_is_found() {
        repository.result = List.of();

        service.generateReport(request());

        assertThat(notifier.received).hasSize(1);
    }

    @Test
    void rejects_a_request_with_invalid_date_range() {
        assertThatThrownBy(() -> new TransactionStatisticsRequest(TO, FROM, "Kwik-E-Mart"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("fromDate should before toDate");
    }

    @Test
    void rejects_a_request_with_missing_values() {
        assertThatThrownBy(() -> new TransactionStatisticsRequest(FROM, TO, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("merchant name can not be null");
    }

    private static TransactionStatisticsRequest request() {
        return new TransactionStatisticsRequest(FROM, TO, "Kwik-E-Mart");
    }

    private static Transaction payment(String id, String amount) {
        return new Transaction(id, FROM.plusMinutes(10), new BigDecimal(amount),
                "Kwik-E-Mart", TransactionType.PAYMENT, null);
    }

    static final class FixedTransactionRepository implements TransactionRepository {
        List<Transaction> result = List.of();

        @Override
        public void save(List<Transaction> transactions) {
        }

        @Override
        public List<Transaction> findValidPayments(String merchant, LocalDateTime fromDate, LocalDateTime toDate) {
            return result;
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
