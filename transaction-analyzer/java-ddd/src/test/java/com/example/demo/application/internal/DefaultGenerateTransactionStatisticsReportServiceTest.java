package com.example.demo.application.internal;

import com.example.demo.application.GenerateTransactionStatisticsReportService;
import com.example.demo.application.QueryValidPaymentTransactionsService;
import com.example.demo.application.TransactionStatisticsRequest;
import com.example.demo.application.TransactionStatisticsResponse;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultGenerateTransactionStatisticsReportServiceTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2020, 8, 20, 12, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2020, 8, 20, 15, 0, 0);

    private final FixedTransactionRepository repository = new FixedTransactionRepository();
    private final QueryValidPaymentTransactionsService queryService = new DefaultQueryValidPaymentTransactionsService(repository, List.of());
    private final GenerateTransactionStatisticsReportService service = new DefaultGenerateTransactionStatisticsReportService(queryService);

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
        public List<Transaction> findByType(TransactionType type) {
            return List.of();
        }

        @Override
        public List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type) {
            return result;
        }
    }
}
