package com.example.demo.domain.service;

import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;
import com.example.demo.port.in.QueryValidPaymentTransactionsPort;
import com.example.demo.port.in.TransactionStatisticsRequest;
import com.example.demo.port.in.TransactionStatisticsResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionStatisticsReportServiceTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2020, 8, 20, 12, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2020, 8, 20, 15, 0, 0);

    private final FixedQueryPort queryPort = new FixedQueryPort();
    private final TransactionStatisticsReportService service = new TransactionStatisticsReportService(queryPort);

    @Test
    void reports_statistics_of_found_transactions() {
        queryPort.result = List.of(
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
        queryPort.result = List.of();

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

    static final class FixedQueryPort implements QueryValidPaymentTransactionsPort {
        List<Transaction> result = List.of();

        @Override
        public List<Transaction> queryValidPaymentTransactions(String merchant, LocalDateTime fromDate, LocalDateTime toDate) {
            return result;
        }
    }
}
