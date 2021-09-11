import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

internal class TransactionTest {
    private lateinit var instance: Transaction;

    @BeforeEach
    fun setup() {
        instance = Transaction(
            "test",
            LocalDateTime.now(),
            BigDecimal.valueOf(5.0),
            "testMerchant",
            TransactionType.PAYMENT,
            ""
        )
    }

    @Test
    fun `verify the Transaction instance`() {
        instance.id shouldBe "test"
        instance.transactedAt shouldBeBefore LocalDateTime.now()
        instance.amount shouldBeEqualComparingTo BigDecimal.valueOf(5.0)
        instance.merchantName shouldBe "testMerchant"
        instance.type shouldBe TransactionType.PAYMENT
        instance.relatedTransactionId shouldBe ""

        instance should {
            it.id shouldBe "test"
        }
    }
}