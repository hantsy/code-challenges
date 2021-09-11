import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

internal class TransactionRepositoryTest {

    @ParameterizedTest
    @MethodSource("provideQueryCriteria")
    fun `test queryByMerchantAndDateRange against mock loader`(
        fromDate: String,
        toDate: String,
        merchant: String,
        n: Int
    ) {
        val loader: TransactionLoader = mockk()
        every { loader.load() } returns fakeTransactionData
        val transactions = TransactionRepository(loader)
            .queryByMerchantAndDateRange(
                merchant,
                fromDate.toLocalDateTime(),
                toDate.toLocalDateTime()
            )
        transactions.size shouldBe n
        verify(exactly = 1) { loader.load() }
        confirmVerified(loader)
    }

    fun provideQueryCriteria(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("20/08/2020 12:00:00", "20/08/2020 13:00:00", "Kwik-E-Mart", 1),
            Arguments.of("20/08/2020 12:00:00", "20/08/2020 15:00:00", "MacLaren", 2)
        )
    }

    private val fakeTransactionData = Fixtures.transactionData
}