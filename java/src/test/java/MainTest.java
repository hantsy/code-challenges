import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class MainTest {

    @Nested
    class MainTestCase {
        @BeforeEach
        void setUp() {
        }

        @AfterEach
        void tearDown() {
        }

        @Test
        void testMain() {
            Main.main(new String[]{});
        }
    }


    @Nested
    @ExtendWith(MockitoExtension.class)
    class TransactionRepository_InjectedMockLoaderTestCase {

        @Mock TransactionLoader mockedLoader;

        @InjectMocks TransactionRepository repository;

        @Test
        void testQuery_MockLoader() throws IOException {
            String fromDate ="20/08/2020 12:00:00";
            String toDate="20/08/2020 13:00:00";
            String merchant="Kwik-E-Mart";
            given(mockedLoader.load()).willReturn(Fixtures.transactionData());

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
    class TransactionRepository_MockLoaderTestCase {

        @ParameterizedTest
        @MethodSource("provideQueryCriteria")
        void testQuery_MockLoader(String fromDate, String toDate, String merchant, int n) throws IOException {
            var mockedLoader = mock(TransactionLoader.class);
            given(mockedLoader.load()).willReturn(Fixtures.transactionData());

            var transactions = new TransactionRepository(mockedLoader)
                    .queryByMerchantAndDateRange(
                            merchant,
                            LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    );
            assertThat(transactions.size()).isEqualTo(n);
            verify(mockedLoader, times(1)).load();
            verifyNoMoreInteractions(mockedLoader);
        }


        Stream<Arguments> provideQueryCriteria(){
            return  Stream.of(
                    Arguments.of("20/08/2020 12:00:00", "20/08/2020 13:00:00", "Kwik-E-Mart", 1),
                    Arguments.of("20/08/2020 12:00:00", "20/08/2020 15:00:00", "MacLaren", 2)
            );
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class TransactionRepository_FakeLoaderTestCase{

        @ParameterizedTest
        @MethodSource("provideQueryCriteria")
        void testQuery_FakeLoader( String fromDate, String toDate, String merchant, int n){
            var loader = new FakeTransactionLoader();
            var transactions = new TransactionRepository(loader)
                    .queryByMerchantAndDateRange(
                            merchant,
                            LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    );
            assertThat(transactions.size()).isEqualTo(n);
        }


        Stream<Arguments> provideQueryCriteria(){
            return  Stream.of(
                    Arguments.of("20/08/2020 12:00:00", "20/08/2020 13:00:00", "Kwik-E-Mart", 1),
                    Arguments.of("20/08/2020 12:00:00", "20/08/2020 15:00:00", "MacLaren", 2)
            );
        }

    }

    class FakeTransactionLoader implements TransactionLoader {
        @Override
        public List<Transaction> load() throws IOException {
            return Fixtures.transactionData();
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
    static class Fixtures{
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

    @Nested
    class TransactionLoaderTestCase {

        @TestFactory
        List<DynamicTest> testLoad() throws IOException {
            return List.of(
                    dynamicTest("test1", () -> {
                        var data = """
                                ID, Date, Amount, Merchant, Type, Related Transaction
                                WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
                                YGXKOEIA, 20/08/2020 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,
                                """;
                        var loader = new InputStreamTransactionLoader(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
                        var loadedData = loader.load();
                        assertThat(loadedData.size()).isEqualTo(2);

                    }),
                    dynamicTest("test2", () -> {
                        var data = """
                                ID, Date, Amount, Merchant, Type, Related Transaction
                                WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
                                """;
                        var loader = new InputStreamTransactionLoader(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
                        var loadedData = loader.load();
                        assertThat(loadedData.size()).isEqualTo(1);
                    })
            );

        }

    }


    @Nested
    class TransactionTypeTestCase {

        @Test
        void verifyTransactionTypePayment() {
            assertThat(TransactionType.valueOf("PAYMENT")).isEqualTo(TransactionType.PAYMENT);
        }

        @Test
        void verifyTransactionTypeReversal() {
            assertThat(TransactionType.valueOf("REVERSAL")).isEqualTo(TransactionType.REVERSAL);
        }
    }


    @Nested
    class TransactionTestCase {

        Transaction instance;

        @BeforeEach
        void setUp() {
            instance = new Transaction("test",
                    LocalDateTime.now(),
                    BigDecimal.valueOf(5.0),
                    "testMerchant",
                    TransactionType.PAYMENT,
                    "");
        }

        @Test
        void verifyInstance() {
            assertThat(instance.id()).isEqualTo("test");
            assertThat(instance.type()).isEqualTo(TransactionType.PAYMENT);
            assertThat(instance.transactedAt()).isBefore(LocalDateTime.now());
            assertThat(instance.relatedTransactionId()).isEmpty();
            assertThat(instance.amount()).isCloseTo(BigDecimal.valueOf(5.00), Offset.offset(BigDecimal.valueOf(0.01)));
            assertThat(instance.merchantName()).isEqualTo("testMerchant");
        }
    }
}