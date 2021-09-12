package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TransactionRepositoryTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("test against injected mock loader by annotations")
    class InjectedMockLoaderTestCase {

        @Mock
        TransactionLoader mockedLoader;

        @InjectMocks
        InMemoryTransactionRepository repository;

        @Test
        void testQuery_MockLoader() throws IOException {
            String fromDate = "20/08/2020 12:00:00";
            String toDate = "20/08/2020 13:00:00";
            String merchant = "Kwik-E-Mart";

            given(mockedLoader.load()).willReturn(Fixtures.transactionData());
            //this.repository.persist(Fixtures.transactionData());

            var transactions = this.repository
                    .queryByMerchantAndDateRange(
                            merchant,
                            LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    );
            assertThat(transactions.size()).isEqualTo(1);
            verify(mockedLoader, times(1)).load();
            verifyNoMoreInteractions(mockedLoader);
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("test against manual mock loader")
    class ManualMockLoaderTestCase {

        @ParameterizedTest
        @MethodSource("provideQueryCriteria")
        void testQuery_MockLoader(String fromDate, String toDate, String merchant, int n) throws IOException {
            var mockedLoader = mock(TransactionLoader.class);
            given(mockedLoader.load()).willReturn(Fixtures.transactionData());

            var transactions = new InMemoryTransactionRepository(mockedLoader)
                    .queryByMerchantAndDateRange(
                            merchant,
                            LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    );
            assertThat(transactions.size()).isEqualTo(n);
            verify(mockedLoader, times(1)).load();
            verifyNoMoreInteractions(mockedLoader);
        }


        Stream<Arguments> provideQueryCriteria() {
            return Stream.of(
                    Arguments.of("20/08/2020 12:00:00", "20/08/2020 13:00:00", "Kwik-E-Mart", 1),
                    Arguments.of("20/08/2020 12:00:00", "20/08/2020 15:00:00", "MacLaren", 2)
            );
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("test against manual mock loader")
    class FakeLoaderTestCase {

        @ParameterizedTest
        @MethodSource("provideQueryCriteria")
        void testQuery_FakeLoader(String fromDate, String toDate, String merchant, int n) throws IOException {
            var loader = new FakeTransactionLoader();
            var transactions = new InMemoryTransactionRepository(loader)
                    .queryByMerchantAndDateRange(
                            merchant,
                            LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    );
            assertThat(transactions.size()).isEqualTo(n);
        }


        Stream<Arguments> provideQueryCriteria() {
            return Stream.of(
                    Arguments.of("20/08/2020 12:00:00", "20/08/2020 13:00:00", "Kwik-E-Mart", 1),
                    Arguments.of("20/08/2020 12:00:00", "20/08/2020 15:00:00", "MacLaren", 2)
            );
        }

    }

    /*
    WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
    YGXKOEIA, 20/08/2020 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,
    LFVCTEYM, 20/08/2020 12:50:02, 5.00, MacLaren, PAYMENT,
    SUOVOISP, 20/08/2020 13:12:22, 5.00, Kwik-E-Mart, PAYMENT,
    AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
    JYAPKZFZ, 20/08/2020 14:07:10, 99.50, MacLaren, PAYMENT,
    */
    static class Fixtures {
        public static List<Transaction> transactionData() {
            return List.of(
                    new Transaction("WLMFRDGD",
                            LocalDateTime.parse("20/08/2020 12:45:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            BigDecimal.valueOf(59.99),
                            "Kwik-E-Mart",
                            TransactionType.PAYMENT,
                            ""),
                    new Transaction("YGXKOEIA",
                            LocalDateTime.parse("20/08/2020 12:46:17", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            BigDecimal.valueOf(10.95),
                            "Kwik-E-Mart",
                            TransactionType.PAYMENT,
                            ""),
                    new Transaction("LFVCTEYM",
                            LocalDateTime.parse("20/08/2020 12:50:02", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            BigDecimal.valueOf(5.00),
                            "MacLaren",
                            TransactionType.PAYMENT,
                            ""),
                    new Transaction("SUOVOISP",
                            LocalDateTime.parse("20/08/2020 13:12:22", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            BigDecimal.valueOf(5.00),
                            "Kwik-E-Mart",
                            TransactionType.PAYMENT,
                            ""),
                    new Transaction("AKNBVHMN",
                            LocalDateTime.parse("20/08/2020 13:14:11", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            BigDecimal.valueOf(10.95),
                            "Kwik-E-Mart",
                            TransactionType.REVERSAL,
                            "YGXKOEIA"),
                    new Transaction("JYAPKZFZ",
                            LocalDateTime.parse("20/08/2020 14:07:10", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            BigDecimal.valueOf(99.50),
                            "MacLaren",
                            TransactionType.PAYMENT,
                            "")
            );
        }
    }


    static class FakeTransactionLoader implements TransactionLoader {
        @Override
        public List<Transaction> load() {
            return Fixtures.transactionData();
        }
    }
}