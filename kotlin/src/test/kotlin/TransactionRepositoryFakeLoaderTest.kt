import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

internal class TransactionRepositoryFakeLoaderTest {

    @ParameterizedTest
    @MethodSource("provideQueryCriteria")
    fun `test queryByMerchantAndDateRange against fakeloader loader`(
        fromDate: String,
        toDate: String,
        merchant: String,
        n: Int
    ) {
        val loader: TransactionLoader = object : TransactionLoader {
            override fun load(): List<Transaction> = fakeTransactionData
        }
        val transactions = TransactionRepository(loader)
            .queryByMerchantAndDateRange(
                merchant,
                LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            )
        transactions.size shouldBe n
    }

    fun provideQueryCriteria(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("20/08/2020 12:00:00", "20/08/2020 13:00:00", "Kwik-E-Mart", 1),
            Arguments.of("20/08/2020 12:00:00", "20/08/2020 15:00:00", "MacLaren", 2)
        )
    }

    private val fakeTransactionData = Fixtures.transactionData
}