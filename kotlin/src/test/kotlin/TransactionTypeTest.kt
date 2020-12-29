import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class TransactionTypeTest {

    @Test
    fun `parse "PAYMENT" should be ok`() {
        TransactionType.valueOf("PAYMENT") shouldBe TransactionType.PAYMENT
    }

    @Test
    fun `parse "REVERSAL" should be ok`() {
        TransactionType.valueOf("REVERSAL") shouldBe TransactionType.REVERSAL
    }
}