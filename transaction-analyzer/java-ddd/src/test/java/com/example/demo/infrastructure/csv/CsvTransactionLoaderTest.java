package com.example.demo.infrastructure.csv;

import com.example.demo.domain.model.TransactionType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvTransactionLoaderTest {

    @Test
    void loads_all_transactions_from_the_bundled_csv_file() {
        var loader = new CsvTransactionLoader(getClass().getResourceAsStream("/input.csv"));

        var transactions = loader.load();

        assertThat(transactions).hasSize(6);

        var first = transactions.get(0);
        assertThat(first.id()).isEqualTo("WLMFRDGD");
        assertThat(first.merchantName()).isEqualTo("Kwik-E-Mart");
        assertThat(first.amount()).isEqualByComparingTo("59.99");
        assertThat(first.type()).isEqualTo(TransactionType.PAYMENT);
        assertThat(first.relatedTransactionId()).isNull();

        var reversal = transactions.get(4);
        assertThat(reversal.id()).isEqualTo("AKNBVHMN");
        assertThat(reversal.type()).isEqualTo(TransactionType.REVERSAL);
        assertThat(reversal.relatedTransactionId()).isEqualTo("YGXKOEIA");
    }
}
