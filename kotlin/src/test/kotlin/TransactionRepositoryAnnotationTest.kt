import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.provider.Arguments
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
internal class TransactionRepositoryAnnotationTest {
    @MockK
    lateinit var loader: TransactionLoader

    @InjectMockKs(overrideValues = true, injectImmutable = true)
    lateinit var repository: TransactionRepository

    private val fakeTransactionData = Fixtures.transactionData

    @Test
    fun `test queryByMerchantAndDateRange against mock loader(Annotations)`() {
        val fromDate = "20/08/2020 12:00:00"
        val toDate = "20/08/2020 13:00:00"
        val merchant = "Kwik-E-Mart"
        every { loader.load() } returns fakeTransactionData
        val transactions = repository
            .queryByMerchantAndDateRange(
                merchant,
                LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            )
        transactions.size shouldBe 1
        verify(exactly = 1) { loader.load() }
        confirmVerified(loader)
    }
}